package com.liliflora.controller;

import com.liliflora.dto.ResponseDto;
import com.liliflora.dto.UserRequestDto;
import com.liliflora.jwt.JwtToken;
import com.liliflora.service.MailSendService;
import com.liliflora.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailSendService mailService;

    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody @Valid UserRequestDto.Signup requestDto) {   // @RequestBody 는 json 객체로 넘어오는 것을 받아준다
        log.info("UserController.signup()");
        log.info("email = {}, password = {}, name = {}", requestDto.getEmail(), requestDto.getPassword(), requestDto.getName());

        String result = userService.signup(requestDto);
        if (result.equals("Success")) {
            return ResponseDto.of(HttpStatus.OK, "가입 성공");
        } else {
            return ResponseDto.of(HttpStatus.BAD_REQUEST, "중복된 이메일 입니다.");
        }
    }

    @PostMapping ("/mailSend")
    public String mailSend(@RequestBody @Valid UserRequestDto.EmailRequest emailRequest) {
        log.info("인증 이메일 : " + emailRequest.getEmail());
        return mailService.joinEmail(emailRequest.getEmail());
    }

    @PostMapping ("/mailAuthCheck")
    public String authCheck(@RequestBody @Valid UserRequestDto.EmailRequest requestDto) {
        log.info("인증 번호 : " + requestDto.getAuthNumber());
        return mailService.checkAuthNumber(requestDto);
    }

    @PostMapping("sign-in")
    public JwtToken signIn(@RequestBody UserRequestDto.Signin signInDto) {
        log.info("UserController.signIn()");

        String username = signInDto.getEmail();
        String password = signInDto.getPassword();
        JwtToken jwtToken = userService.signIn(username, password);

        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;    // Access Token 발급
    }

    @PostMapping("/member/test")
    public String test() {
        return "success";
    }

}
