package com.liliflora.controller;

import com.liliflora.dto.WishItemRequestDto;
import com.liliflora.dto.WishItemResponseDto;
import com.liliflora.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // 장바구니 추가
    @PostMapping("/add")
    public WishItemResponseDto.WishItemCheckDto addWishlist(@RequestBody WishItemRequestDto.AddWishItemDto addWishlistDto,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("WishlistController.addWishlist()");

        return wishlistService.addWishlist(addWishlistDto, Long.valueOf(userDetails.getUsername()));
    }

    // 장바구니 조회
    @GetMapping("/my")
    public List<WishItemResponseDto.WishItemCheckDto> myWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("WishlistController.myWishlist()");

        return wishlistService.myWishlist(Long.valueOf(userDetails.getUsername()));
    }

    // 장바구니 수량 변경
    @PutMapping("/update")
    public WishItemResponseDto.WishItemCheckDto updateWishlist(@RequestBody WishItemRequestDto.UpdateWishItemDto updateWishItemDto,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        log.info("WishlistController.updateWishlist()");

        return wishlistService.updateWishlist(updateWishItemDto, Long.valueOf(userDetails.getUsername()));
    }

    // 장바구니 삭제
    @PutMapping("/delete")
    public WishItemResponseDto.WishItemCheckDto deleteWishlist(@RequestBody WishItemRequestDto.UpdateWishItemDto updateWishItemDto,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        log.info("WishlistController.deleteWishlist()");

        return wishlistService.deleteWishlist(updateWishItemDto, Long.valueOf(userDetails.getUsername()));
    }

}
