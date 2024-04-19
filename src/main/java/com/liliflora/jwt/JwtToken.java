package com.liliflora.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// 클라이언트에 토큰을 보내기 위한 클래스
@Builder
@Data
@AllArgsConstructor
public class JwtToken {
    private String grantType;   // JWT 에 대한 인증 타입 -> "Bearer" 인증 방식을 사용할 것
    private String accessToken;
    private String refreshToken;
}
