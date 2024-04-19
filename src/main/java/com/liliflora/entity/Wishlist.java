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
public class Wishlist {
    @Id
    @Column(name = "wishlist_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long wishlistId;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(nullable = false, name = "user_id")
    private User userId;

}