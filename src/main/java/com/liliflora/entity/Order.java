package com.liliflora.entity;

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
    @Column(name = "order_no")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false, name = "user_no")
    private User userNo;

    @Column(nullable = false, name = "payment_date")
    private Date paymentDate;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column
    private String update;

    @Column(nullable = false)
    private String status;

}
