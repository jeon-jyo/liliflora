package com.liliflora.dto;

import com.liliflora.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class UserRequestDto {

    // 회원가입시에 필요한 attribute 만
    @Getter
    public static class signup {
        
        // 이메일 인증 + 이메일 중복
        // 예외처리
        // Duplicate entry

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        //@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        //@Size(min = 8, max = 20, message = "8자 이상 20자 이내로 작성 가능합니다.")
        //@Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;

        @NotBlank(message = "핸드폰번호는 필수 입력 값입니다.")
        //@Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호는 숫자 10~11자리만 가능합니다.")
        private String phone;

        @NotBlank(message = "주소는 필수 입력 값입니다.")
        private String address;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .phone(phone)
                    .build();
        }

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class signin {
        private String email;
        private String password;
    }

}
