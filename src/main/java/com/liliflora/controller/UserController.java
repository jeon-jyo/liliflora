package com.liliflora.controller;

import com.liliflora.dto.ResponseDto;
import com.liliflora.dto.UserRequestDto;
import com.liliflora.jwt.JwtToken;
import com.liliflora.security.UserDetailsImpl;
import com.liliflora.service.MailSendService;
import com.liliflora.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailSendService mailService;

    // 이메일 중복확인 및 인증번호 발송
    @PostMapping ("/mailSend")
    public String mailSend(@RequestBody @Valid UserRequestDto.EmailRequest emailRequestDto) {
        log.info("UserController.mailSend()");
        log.info("인증 이메일 : " + emailRequestDto.getEmail());

        String email = emailRequestDto.getEmail();
        String result = "";
        if (!userService.emailCheck(email)) {
            result = mailService.joinEmail(emailRequestDto.getEmail());
        } else {
            result = "이미 존재하는 이메일 입니다.";
        }
        return result;
    }

    // 인증번호 검사
    @PostMapping ("/mailAuthCheck")
    public String authCheck(@RequestBody @Valid UserRequestDto.EmailRequest emailRequestDto) {
        log.info("UserController.authCheck()");
        log.info("인증 번호 : " + emailRequestDto.getAuthNumber());
        return mailService.checkAuthNumber(emailRequestDto);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody @Valid UserRequestDto.Signup signupDto) {   // @RequestBody 는 json 객체로 넘어오는 것을 받아준다
        log.info("UserController.signup()");
        log.info("email = {}, password = {}, name = {}", signupDto.getEmail(), signupDto.getPassword(), signupDto.getName());

        String result = userService.signup(signupDto);
        if (result.equals("Success")) {
            return ResponseDto.of(HttpStatus.CREATED, "가입 성공");
        } else if (result.equals("Duplicate")) {
            return ResponseDto.of(HttpStatus.CONFLICT, "중복된 이메일 입니다.");
        } else {
            return ResponseDto.of(HttpStatus.BAD_REQUEST, "가입 실패");
        }
    }

    // 로그인
    @PostMapping("/signin")
    public JwtToken signin(@RequestBody UserRequestDto.Signin signinDto) {
        log.info("UserController.signin()");

        String username = signinDto.getEmail();
        String password = signinDto.getPassword();
        JwtToken jwtToken = userService.signin(username, password);
        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;    // Access Token 발급
    }

    /*
    Spring Security 의 인증된 사용자(principal) 정보는 Authentication 객체를 통해 접근됨
    Authentication 객체에서 principal 을 추출하여 해당 필드에 직접 액세스할 수 있음
     */
    // 마이페이지 - 내 정보 조회
    @GetMapping("/myPage")
    public UserRequestDto.MyPage myPage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        log.info("UserController.myPage()");
        log.info(userDetailsImpl.getUsername());

        if (userDetailsImpl != null) {
            UserRequestDto.MyPage dto = userService.myPage(userDetailsImpl.getUser());
            return dto;
        } else {
            // UserDetailsImpl이 널인 경우에 대한 처리
            // 예를 들어 로그인 페이지로 리다이렉트하거나 에러 메시지를 반환할 수 있습니다.
            throw new IllegalStateException("UserDetailsImpl is null");
        }
    }

//    @GetMapping("/myPage")
//    public UserRequestDto.MyPage myPage(@RequestBody UserRequestDto.MyPage myPageDto) {
//        log.info("UserController.myPage()");
//        UserRequestDto.MyPage dto = userService.myPage(myPageDto.getEmail());
//        return dto;
//    }

    // 폰 번호 업데이트
    @PutMapping("/phone")
    public boolean updatePhone(@RequestParam @NotBlank String phone, UserRequestDto.MyPage myPageDto) {
        log.info("UserController.updatePhone()");
        userService.updatePhone(phone, myPageDto.getEmail());
        return true;
    }

    // 주소 업데이트
    @PutMapping("/address")
    public boolean updateAddress(@RequestParam @NotBlank String address, UserRequestDto.MyPage myPageDto) {
        log.info("UserController.updateAddress()");
        userService.updateAddress(address, myPageDto.getEmail());
        return true;
    }

    // 비밀번호 업데이트
    @PutMapping("/password")
    public boolean updatePassword(@RequestBody @Valid UserRequestDto.ChangePhone changePhoneDto, UserRequestDto.MyPage myPageDto) {
        log.info("UserController.updatePassword()");
        userService.updatePassword(changePhoneDto, myPageDto.getEmail());
        return true;
    }
    
}
