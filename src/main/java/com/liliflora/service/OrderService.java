package com.liliflora.service;

import com.liliflora.dto.*;
import com.liliflora.entity.*;
import com.liliflora.repository.*;
import com.liliflora.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;
    private final WishlistRepository wishlistRepository;
    private final EncryptUtil encryptUtil;

    private final OrderPayingRepository orderPayingRepository;
    private final StockRepository stockRepository;
    private final Queue<String> queue = new LinkedList<>();
    private final ListOperations<String, String> listOps;

    // 상품 주문 진입
    @Transactional
    public long orderProduct(OrderItemRequestDto.OrderProductDto orderProductDto, Long userId) throws Exception {
        log.info("OrderService.orderProduct()");

        long orderProductId = orderProductDto.getProductId();
        int orderQuantity = orderProductDto.getQuantity();

        Product product = productRepository.findById(orderProductId)
                .orElseThrow(() -> new NotFoundException("Product not found " + orderProductId));

        // redis 재고 확인
        Stock stock = stockRepository.findById(orderProductId)
                .orElseGet(() -> {
                    // 없으면 새로운 Stock 객체 생성
                    Stock newStock = new Stock(orderProductId, product.getQuantity());
                    return stockRepository.save(newStock); // 새로운 재고 redis 저장하고 반환
                });

        if (stock.getQuantity() < orderQuantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        stock.decreaseQuantity(orderQuantity);  // redis 재고 감소
        stockRepository.save(stock);

        // 주문
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        OrderStatus orderStatus = createOrderStatus();

        Order order = Order.builder()
                .user(user)
                .amount(product.getPrice() * orderQuantity)
                .orderStatus(orderStatus)
                .build();

        orderRepository.save(order);

        // 주문 상품
        createOrderProduct(order, product, orderQuantity);

        // 20% 고객 이탈
        if (isTwentyPercent()) {
            leaveOrder(order.getOrderId());
        }

        return order.getOrderId();
    }

    // 20% 계산
    @Transactional
    public boolean isTwentyPercent() {
        Random random = new Random();

        // 0에서 99 사이의 랜덤한 정수 생성
        int randomNumber = random.nextInt(100);

        // 20% 확률로 true 반환
        return randomNumber < 20;
    }

    // 이탈
    @Transactional
    public void leaveOrder(Long userId) {
        Order order = orderRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        stockRestore(order);
    }

    // 타임아웃 확인
    @Transactional
    public void checkFailedOrder() {
        // ORDERED("주문생성") 이후 10분이 넘은 주문들 -> FAILED("주문실패")
        List<Order> orderedOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.ORDERED, LocalDateTime.now().minusMinutes(10));

        for (Order order : orderedOrders) {
            stockRestore(order);
        }
    }

    // redis 재고 복구
    @Transactional
    public void stockRestore(Order order) {
        order.getOrderStatus().updateFailed();

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
        for (OrderItem orderItem : orderItems) {
            long productId = orderItem.getProduct().getProductId();
            int quantity = orderItem.getQuantity();

            Stock stock = stockRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Stock not found"));
            System.out.println("삭제 전 : " + stock.getQuantity());

            stock.increaseQuantity(quantity);
            stockRepository.save(stock);
            System.out.println("삭제 후 : " + stock.getQuantity());
        }
    }

    // 결제
    @Transactional
    public OrderResponseDto.OrderCheckDto orderPayment(Long orderId, Long userId) {
        log.info("OrderService.orderPayment()");

        // 20% 고객 이탈
        if (isTwentyPercent()) {
            leaveOrder(orderId);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        order.getOrderStatus().updatePayment();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 주문서
        UserResponseDto.MyPageDto myPageDto = orderUserDetail(user);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
                .map(OrderItemResponseDto.OrderItemCheckDto::fromEntity)
                .toList();

        return OrderResponseDto.OrderCheckDto.fromEntity(order, myPageDto, orderItemCheckDtos);
    }

    // 일반 상품 주문
    @Transactional
    public OrderResponseDto.OrderCheckDto orderProductOrigin(OrderItemRequestDto.OrderProductDto orderProductDto, Long userId) {
        log.info("OrderService.orderProductOrigin()");

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

        OrderStatus orderStatus = createOrderStatus();

        Order order = Order.builder()
                .user(user)
                .amount(product.getPrice() * orderQuantity)
                .orderStatus(orderStatus)
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

    // 주문 상태 추가
    @Transactional
    protected OrderStatus createOrderStatus() {
        OrderStatus orderStatus = OrderStatus.builder()
                .status(OrderStatusEnum.ORDERED)
                .build();

        orderStatusRepository.save(orderStatus);
        return orderStatus;
    }

    // 주문 상품 추가
    @Transactional
    protected OrderItem createOrderProduct(Order currentOrder, Product product, int orderQuantity) {
        OrderItem orderItem = OrderItem.builder()
                .order(currentOrder)
                .product(product)
                .quantity(orderQuantity)
                .build();

        orderItemRepository.save(orderItem);
        return orderItem;
    }

    // 주문 유저 정보
    @Transactional
    protected UserResponseDto.MyPageDto orderUserDetail(User user) {
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

    // 주문 전체 조회
    @Transactional
    public List<OrderResponseDto.OrderListDto> orderList(Long userId) {
        log.info("OrderService.orderList()");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Order> orders = orderRepository.findAllByUserOrderByPurchaseDateDesc(user);
        List<OrderResponseDto.OrderListDto> orderListDtos = new ArrayList<>();
        for (Order currentOrder : orders) {
            List<OrderItem> orderItems = currentOrder.getOrderItems();
            List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
                    .map(OrderItemResponseDto.OrderItemCheckDto::fromEntity)
                    .toList();

            OrderResponseDto.OrderListDto orderCheckDto =
                    OrderResponseDto.OrderListDto.fromEntity(currentOrder, orderItemCheckDtos);

            orderListDtos.add(orderCheckDto);
        }
        return orderListDtos;
    }

    // 주문 상세 조회
    @Transactional
    public OrderResponseDto.OrderCheckDto orderDetail(OrderRequestDto.OrderDetailDto orderDetailDto, Long userId) {
        log.info("OrderService.orderDetail()");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserResponseDto.MyPageDto myPageDto = orderUserDetail(user);

        Order currentOrder = orderRepository.findById(orderDetailDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        List<OrderItem> orderItems = currentOrder.getOrderItems();
        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
                .map(OrderItemResponseDto.OrderItemCheckDto::fromEntity)
                .toList();

        return OrderResponseDto.OrderCheckDto.fromEntity(currentOrder, myPageDto, orderItemCheckDtos);
    }

    // 장바구니 주문
    @Transactional
    public OrderResponseDto.OrderCheckDto orderWishlist(Long userId) {
        log.info("OrderService.orderWishlist()");

        // 장바구니
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Wishlist not found " + userId));

        List<WishItem> wishItems = wishlist.getWishItems();

        List<WishItem> currentWishItems = wishItems.stream()
                .filter(wishItem -> !wishItem.isDeleted()).toList();

        // 주문
        OrderStatus orderStatus = createOrderStatus();

        Order order = Order.builder()
                .user(user)
                .orderStatus(orderStatus)
                .build();

        orderRepository.save(order);

        // 주문 상품
        Order currentOrder = orderRepository.findFirstByUserOrderByPurchaseDateDesc(user)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        int total = 0;
        for (WishItem wishItem : currentWishItems) {
            // 재고 확인
            Product product = productRepository.findById(wishItem.getProduct().getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found " + wishItem.getProduct().getProductId()));

            int orderQuantity = wishItem.getQuantity();
            if (product.getQuantity() < orderQuantity) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            product.decreaseQuantity(orderQuantity);    // 상품 재고 변경
            OrderItem orderItem = createOrderProduct(currentOrder, product, orderQuantity);
            orderItems.add(orderItem);
            total += (product.getPrice() * orderQuantity);

            wishItem.updateDeleted();   // 장바구니에서 삭제
        }
        currentOrder.updateAmount(total);   // 총 금액 업데이트

        // 주문서
        UserResponseDto.MyPageDto myPageDto = orderUserDetail(user);

        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
                .map(OrderItemResponseDto.OrderItemCheckDto::fromEntity)
                .toList();

        return OrderResponseDto.OrderCheckDto.fromEntity(currentOrder, myPageDto, orderItemCheckDtos);
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) throws Exception {
        log.info("OrderService.cancelOrder()");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderStatus orderStatus = order.getOrderStatus();
        if (orderStatus.getStatus() != OrderStatusEnum.ORDERED) {
            throw new Exception("cannot cancel order");
        }
        orderStatus.cancelOrder();  // 주문 취소

        // 상품 재고 복구
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProduct().getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found " + orderItem.getProduct().getProductId()));

            product.increaseQuantity(orderItem.getQuantity());
        }
    }

    // 상품 반품
    @Transactional
    public void returnOrder(Long orderId) throws Exception {
        log.info("OrderService.returnOrder()");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderStatus orderStatus = order.getOrderStatus();
        if (orderStatus.getStatus() != OrderStatusEnum.COMPLETED) {
            throw new Exception("cannot cancel order");
        }

        if (LocalDateTime.now().isAfter(order.getChangedDate().plusDays(1))) {  // 해당 주문이 변경된 후 24시간이 지났는지를 판단
            throw new Exception("cannot return order");
        }

        orderStatus.returnOrder();  // 상품 반품
    }

    // 주문 상태 변경 - 자정마다 실행
    @Transactional
    public void updateOrderStatus() {
        // ORDERED("결제완료") 이후 1일이 넘은 주문들 -> SHIPPING("배송중")
        List<Order> orderedOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.ORDERED, LocalDateTime.now().minusDays(1));

        for (Order order : orderedOrders) {
            order.getOrderStatus().updateShipping();
        }

        // SHIPPING("배송중") 이후 1일이 넘은 주문들 -> COMPLETED("배송완료")
        List<Order> shippingOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.SHIPPING, LocalDateTime.now().minusDays(1));

        for (Order order : shippingOrders) {
            order.getOrderStatus().updateCompleted();
        }

        // RETURNING("반품중") 이후 1일이 넘은 주문들 -> RETURNED("반품완료")
        List<Order> returningOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.RETURNING, LocalDateTime.now().minusDays(1));

        // 재고 복구
        for (Order order : returningOrders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                Product product = productRepository.findById(orderItem.getProduct().getProductId())
                        .orElseThrow(() -> new NotFoundException("Product not found " + orderItem.getProduct().getProductId()));

                product.increaseQuantity(orderItem.getQuantity());
            }
            order.getOrderStatus().updateReturned();
        }
    }

}
