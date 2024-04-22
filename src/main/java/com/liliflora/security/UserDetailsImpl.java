package com.liliflora.security;

import com.liliflora.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// 사용자의 인증 및 권한 정보를 제공
// record UserDetailsImpl(User user)
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /*
    security

    멤버가 가지고 있는 권한(authority) 목록을 SimpleGrantedAuthority 로 변환하여 반환
    나머지 Override 메서드들 전부 true 로 반환하도록 설정
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = user.getRole().getRole();

        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return Long.toString(user.getUserId()); // 토큰 발행시 pk 값으로 key 설정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}