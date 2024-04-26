package com.liliflora.controller;

import com.liliflora.dto.OrderItemRequestDto;
import com.liliflora.dto.OrderResponseDto;
import com.liliflora.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 상품 주문
    @PostMapping("/product")
    public OrderResponseDto.OrderCheckDto orderProduct(@RequestBody OrderItemRequestDto.OrderProductDto orderProductDto,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderProduct()");

        return orderService.orderProduct(orderProductDto, Long.valueOf(userDetails.getUsername()));
    }
}
