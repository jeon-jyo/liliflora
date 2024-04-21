package com.liliflora.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ResponseDto<D> {
    private HttpStatus status;  // 응답의 상태
    private String message; // 해당 상태에 대한 메시지

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private D data; // 응답 데이터가 담기며, 필요에 따라 생략

    public static<D> ResponseDto<D> of(HttpStatus status, String message) {
        return ResponseDto.<D>builder()
                .status(status)
                .message(message)
                .build();
    }

    public static<D> ResponseDto<D> of(HttpStatus status, String message, D data) {
        return ResponseDto.<D>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
