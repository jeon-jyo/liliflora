package com.liliflora.dto;

import com.liliflora.entity.User;
import com.liliflora.entity.WishItem;
import com.liliflora.entity.Wishlist;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class WishlistRequestDto {

    @Getter
    @Setter
    @Builder
    public static class MyWishlistDto {
        private User user;

        private List<WishItem> wishItems;

        public Wishlist toEntity(MyWishlistDto wishlistDto) {
            return Wishlist.builder()
                    .user(wishlistDto.getUser())
                    .build();
        }

        public static WishlistRequestDto.MyWishlistDto fromEntity(Wishlist wishlist) {
            return MyWishlistDto.builder()
                    .user(wishlist.getUser())
                    .wishItems(wishlist.getWishItems())
                    .build();
        }
    }
}
