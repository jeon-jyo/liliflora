package com.liliflora.dto;

import com.liliflora.entity.Order;
import com.liliflora.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderResponseDto {

    @Setter
    @Getter
    @Builder
    public static class OrderCheckDto {
        private long orderId;

        private int amount;

        private String purchaseDate;

        private String changedDate;

        private long orderStatusId;

        private String orderStatus;

        private UserResponseDto.MyPageDto user;

        private List<OrderItemResponseDto.OrderItemCheckDto> orderItems;

        public static OrderResponseDto.OrderCheckDto fromEntity(Order order, UserResponseDto.MyPageDto user, List<OrderItemResponseDto.OrderItemCheckDto> orderItems) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            return OrderCheckDto.builder()
                    .orderId(order.getOrderId())
                    .amount(order.getAmount())
                    .purchaseDate(order.getPurchaseDate().format(formatter))
                    .changedDate(order.getChangedDate().format(formatter))
                    .orderStatusId(order.getOrderStatus().getOrderStatusId())
                    .orderStatus(order.getOrderStatus().getStatus().getStatus())
                    .user(user)
                    .orderItems(orderItems)
                    .build();
        }
    }

    @Setter
    @Getter
    @Builder
    public static class OrderListDto {
        private long orderId;

        private int amount;

        private String purchaseDate;

        private String changedDate;

        private long orderStatusId;

        private String orderStatus;

        private List<OrderItemResponseDto.OrderItemCheckDto> orderItems;

        public static OrderResponseDto.OrderListDto fromEntity(Order order, List<OrderItemResponseDto.OrderItemCheckDto> orderItems) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            return OrderListDto.builder()
                    .orderId(order.getOrderId())
                    .amount(order.getAmount())
                    .purchaseDate(order.getPurchaseDate().format(formatter))
                    .changedDate(order.getChangedDate().format(formatter))
                    .orderStatusId(order.getOrderStatus().getOrderStatusId())
                    .orderStatus(order.getOrderStatus().getStatus().getStatus())
                    .orderItems(orderItems)
                    .build();
        }
    }

}
