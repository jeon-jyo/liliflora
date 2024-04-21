package com.liliflora.service;

import com.liliflora.dto.UserRequestDto;
import com.liliflora.entity.User;
import com.liliflora.jwt.JwtToken;
import com.liliflora.jwt.JwtTokenProvider;
import com.liliflora.repository.UserRepository;
import com.liliflora.util.EncryptUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EncryptUtil encryptUtil;

    // 이메일 중복확인
    public boolean emailCheck(String email) {
        String encryptedEmail = encryptUtil.encrypt(email);

        /*
        존재 여부를 표현하는 래퍼(wrapper) 클래스
        null 을 반환하는 메서드의 반환 값을 대체하거나, NullPointerException 을 방지
         */
        Optional<User> findUser = userRepository.findByEmail(encryptedEmail);
        return findUser.isPresent();
    }

    // 회원가입
    public String signup(UserRequestDto.Signup requestDto) {
        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 이름, 이메일, 전화번호, 주소 암호화
        String encryptedEmail = encryptUtil.encrypt(requestDto.getEmail());
        String encryptedName = encryptUtil.encrypt(requestDto.getName());
        String encryptedPhone = encryptUtil.encrypt(requestDto.getPhone());
        String encryptedAddress = encryptUtil.encrypt(requestDto.getAddress());

        User user = User.builder()
                .email(encryptedEmail)
                .password(hashedPassword)
                .name(encryptedName)
                .phone(encryptedPhone)
                .address(encryptedAddress)
                .build();

        try {
            userRepository.save(user);
            return "Success";
        } catch (DataIntegrityViolationException e) {   // 중복된 이메일 주소로 회원가입 시도한 경우 예외 처리
            return "Duplicate";
        }
    }

    // 로그인
    @Transactional
    public JwtToken signin(String email, String password) {

        String encryptedEmail = encryptUtil.encrypt(email);

        // ----- 해싱 -----
        // 데이터베이스에서 암호화된 이메일을 기반으로 사용자 검색
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 제공된 비밀번호와 데이터베이스에 저장된 해시된 비밀번호를 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // ----- jwt -----
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        // jwt 토큰의 username 을 암호화한 이메일로 설정
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(encryptedEmail, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 User 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }

}
