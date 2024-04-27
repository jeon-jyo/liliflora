package com.liliflora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "order_status")
public class OrderStatus {

    @Id
    @Column(name = "order_status_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderStatusId;

    @Enumerated(value = EnumType.STRING)
    private OrderStatusEnum status;

    @OneToMany(mappedBy = "orderStatus", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

}
