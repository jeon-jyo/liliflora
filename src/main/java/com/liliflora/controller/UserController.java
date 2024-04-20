package com.liliflora.controller;

import com.liliflora.dto.ResponseDto;
import com.liliflora.dto.UserRequestDto;
import com.liliflora.jwt.JwtToken;
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

    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody @Valid UserRequestDto.signup requestDto) {   // @RequestBody 는 json 객체로 넘어오는 것을 받아준다
        log.info("UserController.signup()");
        log.info("email = {}, password = {}, name = {}", requestDto.getEmail(), requestDto.getPassword(), requestDto.getName());

//        try{
//            userService.signup(requestDto);
//            return ResponseDto.of(HttpStatus.OK);
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            return ResponseDto.of(HttpStatus.BAD_REQUEST);
//        }

        if(userService.signup(requestDto).equals("Success")) {
            return ResponseDto.of(HttpStatus.Series.SUCCESSFUL);
        }
        return ResponseDto.of(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody UserRequestDto.signin signInDto) {
        log.info("UserController.signIn()");

        String username = signInDto.getEmail();
        String password = signInDto.getPassword();
        JwtToken jwtToken = userService.signIn(username, password);

        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;    // Access Token 발급
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }

}
