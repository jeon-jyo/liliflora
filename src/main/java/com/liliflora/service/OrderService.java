package com.liliflora.service;

import com.liliflora.dto.OrderItemRequestDto;
import com.liliflora.dto.OrderItemResponseDto;
import com.liliflora.dto.OrderResponseDto;
import com.liliflora.dto.UserResponseDto;
import com.liliflora.entity.*;
import com.liliflora.repository.*;
import com.liliflora.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WishlistRepository wishlistRepository;
    private final EncryptUtil encryptUtil;

    // 상품 주문
    @Transactional
    public OrderResponseDto.OrderCheckDto orderProduct(OrderItemRequestDto.OrderProductDto orderProductDto, Long userId) {
        log.info("OrderService.orderProduct()");

        // 재고 확인
        Product product = productRepository.findById(orderProductDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found " + orderProductDto.getProductId()));

        int orderQuantity = orderProductDto.getQuantity();
        if (product.getQuantity() < orderQuantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        // 주문
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = Order.builder()
                .user(user)
                .amount(product.getPrice() * orderQuantity)
                .status(OrderStatusEnum.ORDERED)
                .build();

        orderRepository.save(order);

        // 주문 상품
        Order currentOrder = orderRepository.findFirstByUserOrderByPurchaseDateDesc(user)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        product.decreaseQuantity(orderQuantity);    // 상품 재고 변경
        createOrderProduct(currentOrder, product, orderQuantity);

        // 주문서
        UserResponseDto.MyPageDto myPageDto = orderUserDetail(user);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(currentOrder);

        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
                .map(OrderItemResponseDto.OrderItemCheckDto::fromEntity)
                .toList();

        return OrderResponseDto.OrderCheckDto.fromEntity(currentOrder, myPageDto, orderItemCheckDtos);
    }

    // 주문 상품 추가
    private OrderItem createOrderProduct(Order currentOrder, Product product, int orderQuantity) {
        OrderItem orderItem = OrderItem.builder()
                .order(currentOrder)
                .product(product)
                .quantity(orderQuantity)
                .build();

        orderItemRepository.save(orderItem);
        return orderItem;
    }

    // 주문서 유저 정보
    private UserResponseDto.MyPageDto orderUserDetail(User user) {
        String email = encryptUtil.decrypt(user.getEmail());
        String name = encryptUtil.decrypt(user.getName());
        String phone = encryptUtil.decrypt(user.getPhone());
        String address = encryptUtil.decrypt(user.getAddress());

        return UserResponseDto.MyPageDto.builder()
                .email(email)
                .name(name)
                .phone(phone)
                .address(address)
                .build();
    }
}
