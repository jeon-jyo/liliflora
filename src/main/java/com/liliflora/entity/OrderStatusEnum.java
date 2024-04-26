package com.liliflora.entity;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    ORDERED("결제완료"),
    SHIPPING("배송중"),
    COMPLETED("배송완료"),
    CANCELLED("취소완료"),
    RETURNING("반품중"),
    RETURNED("반품완료"),
    ;

    OrderStatusEnum(String status) {}
}
