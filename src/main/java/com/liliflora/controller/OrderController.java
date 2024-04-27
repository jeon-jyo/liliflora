package com.liliflora.controller;

import com.liliflora.dto.OrderItemRequestDto;
import com.liliflora.dto.OrderRequestDto;
import com.liliflora.dto.OrderResponseDto;
import com.liliflora.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 주문 전체 조회
    @GetMapping("/my")
    public List<OrderResponseDto.OrderListDto> myOrderList(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.myOrderList()");

        return orderService.myOrderList(Long.valueOf(userDetails.getUsername()));
    }

    // 주문 상세 조회
    @GetMapping("/detail")
    public OrderResponseDto.OrderCheckDto orderDetail(@RequestBody OrderRequestDto.OrderDetailDto orderDetailDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderDetail()");

        return orderService.orderDetail(orderDetailDto, Long.valueOf(userDetails.getUsername()));
    }
    
}
