package com.liliflora.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ResponseDto<D> {
    private int resultCode;
    // private String resultMsg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private D data;

    public static<D> ResponseDto<D> of(D data) {
        return new ResponseDto<>(HttpStatus.OK.value(), data);
    }
}
