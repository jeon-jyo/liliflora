package com.liliflora.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserResponseDto {

    // 마이페이지 - 내 정보 조회
    @Getter
    @Setter
    @Builder
    public static class MyPageDto {
        private String email;
        private String name;
        private String phone;
        private String address;
    }
}
