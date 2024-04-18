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
public class Basket {
    @Id
    @Column(name = "basket_no")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long basketNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false, name = "user_no")
    private User userNo;

    @Column(nullable = false)
    private String status;

}
