package com.liliflora.security;

import com.liliflora.entity.User;
import com.liliflora.repository.UserRepository;
import com.liliflora.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final EncryptUtil encryptUtil;

    /*
    User Entity -> UserDetailsImpl
    UserDetails 를 새롭게 implements 한 UserDetailsImpl 의 객체를 사용
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService.loadUserByUsername()");

        String encryptedEmail = encryptUtil.encrypt(email);

        // ----- 해싱 -----
        // 데이터베이스에서 암호화된 이메일을 기반으로 사용자 검색
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + email));

        return new UserDetailsImpl(user);
    }

    // 해당하는 User(시큐리티) 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(UserDetailsImpl userDetailsImpl) {
        log.info("CustomUserDetailsService.createUserDetails()");
        return org.springframework.security.core.userdetails.User.builder()
                .username(userDetailsImpl.getUsername())
                .password(userDetailsImpl.getPassword())
                .authorities(userDetailsImpl.getAuthorities())
                .build();
    }
}
