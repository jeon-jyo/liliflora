package com.liliflora.dto;

import lombok.Getter;

public class OrderRequestDto {

    @Getter
    public static class OrderDetailDto {
        private long orderId;
    }
}
