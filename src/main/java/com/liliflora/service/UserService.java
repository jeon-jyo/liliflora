package com.liliflora.service;

import com.liliflora.dto.UserRequestDto;
import com.liliflora.entity.User;
import com.liliflora.jwt.JwtToken;
import com.liliflora.jwt.JwtTokenProvider;
import com.liliflora.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    // private final EncryptUtil encryptor;

    // 회원가입
    public String signup(UserRequestDto.signup requestDto) {

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(requestDto.getPassword());

//        String encryptedEmail = encryptor.encrypt(requestDto.getEmail());
//        String encryptedName = encryptor.encrypt(requestDto.getName());
//        String encryptedAddress = encryptor.encrypt(requestDto.getPhone());
//        String encryptedAddress = encryptor.encrypt(requestDto.getAddress());

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(hashedPassword)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .address(requestDto.getAddress())
                .build();

        userRepository.save(user);  // extends 한 JpaRepository 에서 제공
        return "Success";
    }

    // 로그인
    @Transactional
    public JwtToken signIn(String email, String password) {
        // ----- 해싱 -----
        // 제공된 이메일을 기반으로 데이터베이스에서 사용자를 검색
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 제공된 비밀번호와 데이터베이스에 저장된 해시된 비밀번호를 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // ----- jwt -----
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 User 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }

}
