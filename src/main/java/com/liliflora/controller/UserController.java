package com.liliflora.controller;

import com.liliflora.dto.ResponseDto;
import com.liliflora.dto.UserRequestDto;
import com.liliflora.jwt.JwtToken;
import com.liliflora.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody @Validated UserRequestDto.signup requestDto) {   // @RequestBody 는 json 객체로 넘어오는 것을 받아준다
        log.info("UserController.signup()");
        log.info("email = {}, password = {}, name = {}", requestDto.getEmail(), requestDto.getPassword(), requestDto.getName());

        if(userService.signup(requestDto).equals("Success")) {
            return ResponseDto.of(HttpStatus.Series.SUCCESSFUL);
        }
        return ResponseDto.of(HttpStatus.BAD_REQUEST);
    }

    /*
    "members/sign-in"  ➡︎ 모든 사용자에게 허용

    "members/test"  ➡︎ USER 권한을 가진 사용자에게 허용

    이제 테스트를 위하여 다음 과정을 거칠 것이다.

    Postman으로 이전에 DB에 저장했던 회원 정보(username, password)를 body에 담아서 "members/sign-in"으로 요청
    성공적으로 Access Token 발급
    발급받은 Access Token을 header에 넣어 "members/test"로 요청
     */

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody UserRequestDto.signin signInDto) {
        System.out.println("signInsignInsignInsignIn");
        log.info("UserController.signIn()");

        String username = signInDto.getEmail();
        String password = signInDto.getPassword();
        System.out.println("아아");
        JwtToken jwtToken = userService.signIn(username, password);
        System.out.println("마마");
        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }

}
