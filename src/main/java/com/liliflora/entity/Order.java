package com.liliflora.entity;

import com.liliflora.entity.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table
public class Order {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User userId;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, name = "purchase_date")
    private Date purchaseDate;

    @Column
    private Date update;

    @Column(nullable = false)
    private String status;

}
