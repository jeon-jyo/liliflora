//package com.liliflora.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Getter
//@Table(name = "product_status")
//public class ProductStatus {
//
//    @Id
//    @Column(name = "product_status_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long productStatusId;
//
//    @Enumerated(value = EnumType.STRING)
//    private ProductStatusEnum status;
//
//    @OneToMany(mappedBy = "productStatus", fetch = FetchType.LAZY)
//    private List<Product> products = new ArrayList<>();
//
//
//    public void startOnSale() {
//        this.status = ProductStatusEnum.START_ON_SALE;
//    }
//
//    public void updateWaiting() {
//        this.status = ProductStatusEnum.WAITING;
//    }
//}
