package com.liliflora.dto;

import com.liliflora.entity.Order;
import com.liliflora.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {

    @Setter
    @Getter
    @Builder
    public static class OrderCheckDto {
        private long orderId;

        private int amount;

        private LocalDateTime purchaseDate;

        private OrderStatusEnum status;

        private UserResponseDto.MyPageDto user;

        private List<OrderItemResponseDto.OrderItemCheckDto> orderItems;

        public static OrderResponseDto.OrderCheckDto fromEntity(Order order, UserResponseDto.MyPageDto user, List<OrderItemResponseDto.OrderItemCheckDto> orderItems) {
            return OrderCheckDto.builder()
                    .orderId(order.getOrderId())
                    .amount(order.getAmount())
                    .purchaseDate(order.getPurchaseDate())
                    .status(order.getStatus())
                    .user(user)
                    .orderItems(orderItems)
                    .build();
        }
    }

}
