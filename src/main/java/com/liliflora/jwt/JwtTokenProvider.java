package com.liliflora.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// Spring Security 와 JWT 토큰을 사용하여 인증과 권한 부여를 처리하는 클래스
// JWT 토큰의 생성, 복호화, 검증 기능을 구현
@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    // application.yml (=.properties) 에서 secret 값 가져와서 key 에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);    // 지정된 키 바이트 배열을 기반으로 새 SecretKey 인스턴스를 만듦
    }

    // Member 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 86400000);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // 토큰의 Claims 에서 권한 정보를 추출

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        // SimpleGrantedAuthority 객체로 변환하여 컬렉션에 추가
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails 를 구현한 class
        // 주체(subject)와 권한 정보, 기타 필요한 정보를 설정
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        // 주체와 권한 정보를 포함한 인증(Authentication) 객체를 생성
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드 (유효성을 확인)
    public boolean validateToken(String token) {
        try {
            // 토큰의 서명 키를 설정하고, 예외 처리를 통해 토큰의 유효성 여부를 판단
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {  // 토큰이 올바른 형식이 아니거나 클레임이 비어있는 경우 등에 발생
            log.info("JWT claims string is empty.", e);
        }
        return false;

        /*
        claim.getSubject()는 주어진 토큰의 클레임에서 "sub" 클레임의 값을 반환
        -> 토큰의 주체를 나타냄. ex) 사용자의 식별자나 이메일 주소
         */
    }


    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }

        /*
        클레임(Claims): 토큰에서 사용할 정보의 조각

        주어진 Access Token 을 복호화하고, 만료된 토큰인 경우에도 Claims 반환
        parseClaimsJws() 메서드가 JWT 토큰의 검증과 파싱을 모두 수행
         */
    }

}
