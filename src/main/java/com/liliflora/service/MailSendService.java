package com.liliflora.service;

import com.liliflora.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

// 인증 번호를 생성하고 이메일을 보내는 서비스를 수행
@Service
public class MailSendService {
    @Autowired
    private JavaMailSender mailSender;
    private int authNumber;

    @Autowired
    private RedisService redisService;

    // 임의의 6자리 양수를 반환
    public void makeRandomNumber() {
        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            randomNumber.append((random.nextInt(10)));
        }

        authNumber = Integer.parseInt(randomNumber.toString());
    }

    // Mail 을 어디서 보내는지, 어디로 보내는지, 인증 번호를 html 형식으로 어떻게 보내는지 작성
    public String joinEmail(String email) {
        makeRandomNumber();
        String setFrom = "qwer790488@gmail.com";
        String title = "회원 가입 인증 이메일 입니다.";
        String content =    // html 형식으로 작성
                "Liliflora에 방문해주셔서 감사합니다." + "<br><br>"
                        + "인증 번호는 " + authNumber + " 입니다." + "<br>"
                        + "인증번호를 제대로 입력해주세요.";
        mailSend(setFrom, email, title, content);
        return Integer.toString(authNumber);
    }

    // 이메일을 전송
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();   // JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8"); // 이메일 메시지와 관련된 설정을 수행
            // true 를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);    // 이메일의 발신자 주소 설정
            helper.setTo(toMail);   // 이메일의 수신자 주소 설정
            helper.setSubject(title);   // 이메일의 제목을 설정
            helper.setText(content,true);   // 이메일의 내용 설정, true 설정 (html)
            mailSender.send(message);

            // redis 에 이메일로 인증번호 저장
            redisService.setAuthValues(toMail, Integer.toString(authNumber));
        } catch (MessagingException e) {    // 이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            e.printStackTrace();    // 예외를 기본 오류 스트림에 출력하는 메서드
        }
    }

    public String checkAuthNumber(UserRequestDto.EmailRequest requestDto) {
        String email = requestDto.getEmail();
        String authNumber = requestDto.getAuthNumber();

        if(redisService.checkExistsValue(redisService.getValues(email))) {
            if(redisService.getValues(requestDto.getEmail()).equals(authNumber)) {
                return "인증 성공";
            }
            return "인증번호가 일치하지 않습니다.";
        }
        return "인증번호가 만료되었거나 없습니다.";
    }

}