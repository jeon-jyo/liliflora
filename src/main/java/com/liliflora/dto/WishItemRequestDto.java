package com.liliflora.dto;

import lombok.Getter;

public class WishItemRequestDto {

    @Getter
    public static class addWishItemDto {
        private long productId;
        private int quantity;
    }
}
