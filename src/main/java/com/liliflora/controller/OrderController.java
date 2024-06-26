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

    // 상품 주문 진입
    @PostMapping("/product")
    public long orderProduct(@RequestBody OrderItemRequestDto.OrderProductDto orderProductDto,
                             @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        log.info("OrderController.orderProduct()");

        return orderService.orderProduct(orderProductDto, Long.valueOf(userDetails.getUsername()));
    }

    // 결제
    @PostMapping("/payment/{orderId}")
    public OrderResponseDto.OrderCheckDto orderPayment(@PathVariable("orderId") Long orderId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderPayment()");

        return orderService.orderPayment(orderId, Long.valueOf(userDetails.getUsername()));
    }

    // 일반 상품 주문
    @PostMapping("/productOrigin")
    public OrderResponseDto.OrderCheckDto orderProductOrigin(@RequestBody OrderItemRequestDto.OrderProductDto orderProductDto,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderProductOrigin()");

        return orderService.orderProductOrigin(orderProductDto, Long.valueOf(userDetails.getUsername()));
    }

    // 주문 전체 조회
    @GetMapping("/list")
    public List<OrderResponseDto.OrderListDto> orderList(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderList()");

        return orderService.orderList(Long.valueOf(userDetails.getUsername()));
    }

    // 주문 상세 조회
    @GetMapping("/detail")
    public OrderResponseDto.OrderCheckDto orderDetail(@RequestBody OrderRequestDto.OrderDetailDto orderDetailDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderDetail()");

        return orderService.orderDetail(orderDetailDto, Long.valueOf(userDetails.getUsername()));
    }

    // 장바구니 주문
    @PostMapping("/wishlist")
    public OrderResponseDto.OrderCheckDto orderWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderWishlist()");

        return orderService.orderWishlist(Long.valueOf(userDetails.getUsername()));
    }

    // 주문 취소
    @PutMapping("/cancel/{orderId}")
    public boolean cancelOrder(@PathVariable("orderId") Long orderId) throws Exception {
        log.info("OrderController.cancelOrder()");

        orderService.cancelOrder(orderId);
        return true;
    }

    // 상품 반품
    @PutMapping("/return/{orderId}")
    public boolean returnOrder(@PathVariable("orderId") Long orderId) throws Exception {
        log.info("OrderController.returnOrder()");

        orderService.returnOrder(orderId);
        return true;
    }

}
