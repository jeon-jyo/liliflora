package com.liliflora.dto;

import com.liliflora.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class OrderItemResponseDto {

    @Setter
    @Getter
    @Builder
    public static class OrderItemCheckDto {
        private long orderItemId;

        private long productId;

        private String name;

        private int quantity;

        public static OrderItemResponseDto.OrderItemCheckDto fromEntity(OrderItem orderItem) {
            return OrderItemResponseDto.OrderItemCheckDto.builder()
                    .orderItemId(orderItem.getOrderItemId())
                    .productId(orderItem.getProduct().getProductId())
                    .name(orderItem.getProduct().getName())
                    .quantity(orderItem.getQuantity())
                    .build();
        }
    }


}
