package com.liliflora.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/*
@Value 는 기본적으로 스프링 컨텍스트가 로드된 후에 빈에 주입되기 때문에 static 변수 동작x

이 클래스를 Spring 의 컴포넌트 스캔 대상으로 지정
-> Spring 이 애플리케이션을 시작할 때 이 클래스를 스캔하여 빈으로 등록
 */
@Component
public class EncryptUtil {

    // 암호화 및 복호화에 사용할 비밀 키
    @Value("${encrypt.secret.j}")
    private String secretKey;

    public String encrypt(String valueToEncrypt) {
        try {
            Key key = generateKey();    // 비밀 키 생성
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  // AES 암호화 알고리즘을 사용하는 Cipher 객체를 생성
            cipher.init(Cipher.ENCRYPT_MODE, key);  // 암호화 모드 설정
            byte[] encryptedBytes = cipher.doFinal(valueToEncrypt.getBytes());  // 암호화
            return Base64.getEncoder().encodeToString(encryptedBytes);  // 암호화된 바이트 배열을 Base64로 인코딩하여 문자열로 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);  // 복호화 모드 설정
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue)); // 복호화
            return new String(decryptedBytes);  // 복호화된 바이트 배열을 문자열로 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Key generateKey() {
        return new SecretKeySpec(secretKey.getBytes(), "AES");
    }
}