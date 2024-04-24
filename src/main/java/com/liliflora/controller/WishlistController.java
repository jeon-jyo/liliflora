package com.liliflora.controller;

import com.liliflora.dto.WishItemRequestDto;
import com.liliflora.dto.WishItemResponseDto;
import com.liliflora.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // 장바구니 추가
    @PostMapping("/add")
    public WishItemResponseDto.WishItemCheckDto addWishlist(@RequestBody WishItemRequestDto.addWishItemDto addWishlistDto,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("WishlistController.addWishlist()");

        return wishlistService.addWishlist(addWishlistDto, Long.valueOf(userDetails.getUsername()));
    }

}
