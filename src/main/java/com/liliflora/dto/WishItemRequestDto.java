package com.liliflora.dto;

import lombok.Getter;

public class WishItemRequestDto {

    @Getter
    public static class AddWishItemDto {
        private long productId;
        private int quantity;
    }

    @Getter
    public static class UpdateWishItemDto {
        private long wishItemId;
        private int quantity;
    }
}
