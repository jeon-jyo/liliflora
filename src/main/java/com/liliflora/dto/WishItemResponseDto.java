package com.liliflora.dto;

import com.liliflora.entity.WishItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class WishItemResponseDto {

    @Getter
    @Setter
    @Builder
    public static class WishItemCheckDto {

        private long wishItemId;

        private Long wishlistId;

        private Long productId;

        private int quantity;

        public static WishItemResponseDto.WishItemCheckDto fromEntity(WishItem wishItem) {
            return WishItemResponseDto.WishItemCheckDto.builder()
                    .wishItemId(wishItem.getWishItemId())
                    .wishlistId(wishItem.getWishlist().getWishlistId())
                    .productId(wishItem.getProduct().getProductId())
                    .quantity(wishItem.getQuantity())
                    .build();
        }
    }
}
