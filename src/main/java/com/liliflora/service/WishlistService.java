package com.liliflora.service;

import com.liliflora.dto.WishItemRequestDto;
import com.liliflora.dto.WishItemResponseDto;
import com.liliflora.entity.Product;
import com.liliflora.entity.User;
import com.liliflora.entity.WishItem;
import com.liliflora.entity.Wishlist;
import com.liliflora.repository.ProductRepository;
import com.liliflora.repository.UserRepository;
import com.liliflora.repository.WishItemRepository;
import com.liliflora.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final WishItemRepository wishItemRepository;

    // 장바구니 상품 추가
    @Transactional
    public WishItemResponseDto.WishItemCheckDto addWishlist(WishItemRequestDto.AddWishItemDto addWishlistDto, Long userId) {
        log.info("WishlistService.addWishlist()");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Wishlist not found " + userId));

        Product product = productRepository.findById(addWishlistDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found " + addWishlistDto.getProductId()));

        WishItem wishItem = confirmWishItem(addWishlistDto, wishlist, product);
        return WishItemResponseDto.WishItemCheckDto.fromEntity(wishItem);
    }

    // 장바구니 상품 확인 - 추가 및 수정
    private WishItem confirmWishItem(WishItemRequestDto.AddWishItemDto addWishlistDto, Wishlist wishlist, Product product) {
        Optional<WishItem> currentWishItem =
                wishItemRepository.findWishItemByWishlistAndProductAndDeletedFalse(wishlist, product);

        WishItem wishItem;
        if (currentWishItem.isPresent()) {  // 장바구니에 해당 상품이 존재한다면 수량 업데이트
            wishItem = currentWishItem.get();   // getOne()은 엔티티를 가져오는 동안 지연 로딩을 허용하기 때문에 사용자 식별자로 엔티티를 가져올 때 효율적
            wishItem.increaseQuantity(addWishlistDto.getQuantity());
        } else {    // 존재하지 않는다면 장바구니 추가
            wishItem = WishItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .quantity(addWishlistDto.getQuantity())
                    .build();
        }
        wishItemRepository.save(wishItem);
        return wishItem;
    }

    // 장바구니 조회
    public List<WishItemResponseDto.WishItemCheckDto> myWishlist(Long userId) {
        log.info("WishlistService.myWishlist()");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Wishlist not found " + userId));

        // List<WishItem> 가 자동으로 불러와짐
        List<WishItem> wishItems = wishlist.getWishItems();

        List<WishItem> currentWishItems = wishItems.stream()    // 리스트 -> 스트림
                .filter(wishItem -> !wishItem.isDeleted())  // wishItem 객체에 대해 삭제되지 않은 경우만 필터
                .toList();  // 리스트로 수집

        // map() : 각 WishItem 객체를 새로운 요소(fromEntity)로 매핑
        return currentWishItems.stream().map(WishItemResponseDto.WishItemCheckDto::fromEntity).toList();
    }

    // 장바구니 수량 변경
    public WishItemResponseDto.WishItemCheckDto updateWishlist(WishItemRequestDto.UpdateWishItemDto updateWishItemDto, Long userId) {
        log.info("WishlistService.updateWishlist()");

        WishItem wishItem = wishItemRepository.findById(updateWishItemDto.getWishItemId())
                .orElseThrow(() -> new NotFoundException("WishItem not found " + userId));

        wishItem.updateQuantity(updateWishItemDto.getQuantity());
        wishItemRepository.save(wishItem);
        return WishItemResponseDto.WishItemCheckDto.fromEntity(wishItem);
    }

    // 장바구니 삭제
    public WishItemResponseDto.WishItemCheckDto deleteWishlist(WishItemRequestDto.UpdateWishItemDto updateWishItemDto, Long userId) {
        log.info("WishlistService.deleteWishlist()");

        WishItem wishItem = wishItemRepository.findById(updateWishItemDto.getWishItemId())
                .orElseThrow(() -> new NotFoundException("WishItem not found " + userId));

        wishItem.updateDeleted();
        wishItemRepository.save(wishItem);
        return WishItemResponseDto.WishItemCheckDto.fromEntity(wishItem);
    }
}
