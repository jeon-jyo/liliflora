package com.liliflora.repository;

import com.liliflora.entity.Product;
import com.liliflora.entity.WishItem;
import com.liliflora.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishItemRepository extends JpaRepository<WishItem, Long> {
    Optional<WishItem> findWishItemByWishlistAndProductAndDeletedFalse(Wishlist wishlist, Product product);
}
