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
public class Detail {
    @Id
    @Column(name = "detail_no")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long detailNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false, name = "product_no")
    private Product productNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "basket_no")
    private Basket basketNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "order_no")
    private Order orderNo;

    @Column(nullable = false)
    private int quantity;

}
