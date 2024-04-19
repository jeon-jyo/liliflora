package com.liliflora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table
public class WishItem {

    @Id
    @Column(name = "wish_item_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long wishItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "wishlist_id")
    private Wishlist wishlistId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "product_id")
    private Product productId;

    @Column(nullable = false)
    private int quantity;

}
