package com.liliflora.dto;

import lombok.Getter;

public class OrderItemRequestDto {

    @Getter
    public static class OrderProductDto {
        private long productId;
        private int quantity;
    }
}
