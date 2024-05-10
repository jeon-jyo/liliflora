package com.liliflora.entity;

import lombok.Getter;

@Getter
public enum ProductStatusEnum {

    ON_SALE("상시 판매"),
    WAITING("판매 대기"),
    START_ON_SALE("예약 판매")
    ;

    private final String status;

    ProductStatusEnum(String status) {
        this.status = status;
    }
}
