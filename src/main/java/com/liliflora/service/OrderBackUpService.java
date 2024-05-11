//package com.liliflora.service;
//
//import com.liliflora.dto.*;
//import com.liliflora.entity.*;
//import com.liliflora.repository.*;
//import com.liliflora.util.EncryptUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.security.acls.model.NotFoundException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class OrderBackUpService {
//
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final OrderRepository orderRepository;
//    private final OrderStatusRepository orderStatusRepository;
//    private final OrderItemRepository orderItemRepository;
//    private final WishlistRepository wishlistRepository;
//    private final EncryptUtil encryptUtil;
//
//    private final OrderPayingRepository orderPayingRepository;
//    private final StockRepository stockRepository;
//    private final Queue<String> queue = new LinkedList<>();
//    private final ListOperations<String, String> listOps;
//
//    // 상품 주문 진입
//    @Transactional
//    public String orderProduct(OrderItemRequestDto.OrderProductDto orderProductDto, Long userId) throws Exception {
//        log.info("OrderService.orderProduct()");
//
//        // orderPaying 및 queue 저장
//        String uuid = UUID.randomUUID().toString();
//        OrderPaying orderPaying = new OrderPaying(uuid, orderProductDto.getProductId(), orderProductDto.getQuantity());
//        orderPayingRepository.save(orderPaying);
//        queue.add(uuid);
////        listOps.rightPush("orderQueue", uuid);
//
//        Stock stock = stockRepository.findById(orderProductDto.getProductId())
//                .orElseGet(() -> {
//                    // 없으면 새로운 Stock 객체 생성
//                    Product product = productRepository.findById(orderProductDto.getProductId())
//                        .orElseThrow(() -> new NotFoundException("Product not found " + orderProductDto.getProductId()));
//
//                    Stock newStock = new Stock(orderProductDto.getProductId(), product.getQuantity(), product.getPrice());
//                    return stockRepository.save(newStock); // 새로운 재고 redis 저장하고 반환
//                });
//        stock.decreaseQuantity(orderProductDto.getQuantity());
//        stockRepository.save(stock);
//
//        // 20% 고객 이탈
//        if (isTwentyPercent()) {
//            cancleOrderPaying(uuid, userId);
//        }
//
//        return uuid;
//    }
//
//    // 20% 계산
//    public boolean isTwentyPercent() {
//        Random random = new Random();
//
//        // 0에서 99 사이의 랜덤한 정수 생성
//        int randomNumber = random.nextInt(100);
//
//        // 20% 확률로 true 반환
//        return randomNumber < 20;
//    }
//
//    // 이탈
//    @Transactional
//    public void cancleOrderPaying(String uuid, Long userId) {
//        if (checkQueue()) return;
//    }
//
//    // 1초마다 타임아웃 검사
//    @Scheduled(fixedRate = 1000)
//    public void handleFailedPayment() {
//        if(queue.isEmpty())
//            return;
//
//        while (!queue.isEmpty()) {
//            // Queue 가 비어 있지 않은 동안에만 루프를 실행
//            if (checkQueue()) return;
//        }
//    }
//
////    public boolean isEmpty(String queueName) {
////        Long size = listOps.size(queueName);
////        return size != null && size == 0;
////    }
//
//    // queue 및 orderPaying 삭제
//    private boolean checkQueue() {
//        try {
//            OrderPaying orderPaying = orderPayingRepository.findById(queue.peek())
//                    .orElseThrow(() -> new UsernameNotFoundException("OrderPaying not found"));
//
//            // 문자열을 파싱할 형식 지정
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//            // 문자열을 LocalDateTime 객체로 변환
//            LocalDateTime regDate = LocalDateTime.parse(orderPaying.getRegDate(), formatter);
////            System.out.println(orderPaying);
//
//            if (LocalDateTime.now().minusSeconds(10).isAfter(regDate)) {
//                queue.poll();
//                orderPayingRepository.delete(orderPaying);
////                System.out.println("삭제");
//
//                Stock stock = stockRepository.findById(orderPaying.getProductId())
//                        .orElseThrow(() -> new NotFoundException("Product not found " + orderPaying.getProductId()));
////                System.out.println("삭제 전 : " + stock.getQuantity());
//
//                stock.increaseQuantity(orderPaying.getQuantity());
//                stockRepository.save(stock);
////                System.out.println("삭제 후 : " + stock.getQuantity());
//            } else {
//                // 시간이 안 지나 있으면 return
//                return true;
//            }
//        } catch (Exception e) {
//            // 결제 되어서 OrderPaying 이 없으면 queue 만 삭제
//            queue.poll();
//        }
//        return false;
//    }
//
//    // 결제
//    @Transactional
//    public OrderResponseDto.OrderCheckDto orderPayment(String uuid, Long userId) {
//        log.info("OrderService.orderPayment()");
//
//        // 20% 고객 이탈
//        if (isTwentyPercent()) {
//            cancleOrderPaying(uuid, userId);
//        }
//
//        try {
//            OrderPaying orderPaying = orderPayingRepository.findById(uuid)
//                    .orElseThrow(() -> new UsernameNotFoundException("OrderPaying not found"));
//
//            Product product = productRepository.findById(orderPaying.getProductId())
//                    .orElseThrow(() -> new NotFoundException("Product not found " + orderPaying.getProductId()));
//
//            // 주문
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//            OrderStatus orderStatus = createOrderStatus();
//
//            Order order = Order.builder()
//                    .user(user)
//                    .amount(product.getPrice() * orderPaying.getQuantity())
//                    .orderStatus(orderStatus)
//                    .build();
//
//            orderRepository.save(order);
//
//            // 주문 상품
//            Order currentOrder = orderRepository.findFirstByUserOrderByPurchaseDateDesc(user)
//                .orElseThrow(() -> new NotFoundException("Order not found"));
//
//            product.decreaseQuantity(orderPaying.getQuantity());    // 상품 재고 변경
//            createOrderProduct(currentOrder, product, orderPaying.getQuantity());
//
//            orderPayingRepository.delete(orderPaying);  // orderPaying 삭제
//
//            // 주문서
//            UserResponseDto.MyPageDto myPageDto = orderUserDetail(user);
//
//            List<OrderItem> orderItems = orderItemRepository.findAllByOrder(currentOrder);
//            List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
//                    .map(OrderItemResponseDto.OrderItemCheckDto::fromEntity)
//                    .toList();
//
//            return OrderResponseDto.OrderCheckDto.fromEntity(currentOrder, myPageDto, orderItemCheckDtos);
//
//        } catch (Exception e) {
//            log.info("orderPayment not found");
//            return null;
//        }
//    }
//
//    // 상품 주문
//    @Transactional
//    public OrderResponseDto.OrderCheckDto orderProduct2(OrderItemRequestDto.OrderProductDto orderProductDto, Long userId) {
//        log.info("OrderService.orderProduct()");
//
//        // 재고 확인
//        Product product = productRepository.findById(orderProductDto.getProductId())
//                .orElseThrow(() -> new NotFoundException("Product not found " + orderProductDto.getProductId()));
//
//        int orderQuantity = orderProductDto.getQuantity();
//        if (product.getQuantity() < orderQuantity) {
//            throw new IllegalArgumentException("재고가 부족합니다.");
//        }
//
//        // 주문
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        OrderStatus orderStatus = createOrderStatus();
//
//        Order order = Order.builder()
//                .user(user)
//                .amount(product.getPrice() * orderQuantity)
//                .orderStatus(orderStatus)
//                .build();
//
//        orderRepository.save(order);
//
//        // 주문 상품
//        Order currentOrder = orderRepository.findFirstByUserOrderByPurchaseDateDesc(user)
//                .orElseThrow(() -> new NotFoundException("Order not found"));
//
//        product.decreaseQuantity(orderQuantity);    // 상품 재고 변경
//        createOrderProduct(currentOrder, product, orderQuantity);
//
//        // 주문서
//        UserResponseDto.MyPageDto myPageDto = orderUserDetail(user);
//
//        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(currentOrder);
//        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
//                .map(OrderItemResponseDto.OrderItemCheckDto::fromEntity)
//                .toList();
//
//        return OrderResponseDto.OrderCheckDto.fromEntity(currentOrder, myPageDto, orderItemCheckDtos);
//    }
//
//    // 주문 상태 추가
//    @Transactional
//    protected OrderStatus createOrderStatus() {
//        OrderStatus orderStatus = OrderStatus.builder()
//                .status(OrderStatusEnum.ORDERED)
//                .build();
//
//        orderStatusRepository.save(orderStatus);
//        return orderStatus;
//    }
//
//    // 주문 상태 변경
//    @Transactional
//    protected OrderStatus updaateOrderStatus(OrderStatus orderStatus) {
//        orderStatus.updatePayment();
//        orderStatusRepository.save(orderStatus);
//        return orderStatus;
//    }
//
//    // 주문 상품 추가
//    @Transactional
//    protected OrderItem createOrderProduct(Order currentOrder, Product product, int orderQuantity) {
//        OrderItem orderItem = OrderItem.builder()
//                .order(currentOrder)
//                .product(product)
//                .quantity(orderQuantity)
//                .build();
//
//        orderItemRepository.save(orderItem);
//        return orderItem;
//    }
//
//    // 주문 유저 정보
//    @Transactional
//    protected UserResponseDto.MyPageDto orderUserDetail(User user) {
//        String email = encryptUtil.decrypt(user.getEmail());
//        String name = encryptUtil.decrypt(user.getName());
//        String phone = encryptUtil.decrypt(user.getPhone());
//        String address = encryptUtil.decrypt(user.getAddress());
//
//        return UserResponseDto.MyPageDto.builder()
//                .email(email)
//                .name(name)
//                .phone(phone)
//                .address(address)
//                .build();
//    }
//
//}
