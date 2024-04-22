package com.liliflora.entity;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    USER(Role.USER),
    ADMIN(Role.ADMIN),
            ;

    private final String role;

    UserRoleEnum(String role) {
        this.role = role;
    }

    private static class Role {
        public static final String USER = "회원";
        public static final String ADMIN = "관리자";
    }
}
