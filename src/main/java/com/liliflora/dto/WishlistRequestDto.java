package com.liliflora.dto;

import com.liliflora.entity.User;
import com.liliflora.entity.Wishlist;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class WishlistRequestDto {

    @Getter
    @Setter
    @Builder
    public static class MakeWishlistDto {
        private User user;

        public Wishlist toEntity(MakeWishlistDto wishlistDto) {
            return Wishlist.builder()
                    .user(wishlistDto.getUser())
                    .build();
        }
    }

}
