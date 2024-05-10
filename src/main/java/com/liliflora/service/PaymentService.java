package com.liliflora.service;

import com.liliflora.entity.Order;
import com.liliflora.entity.OrderStatus;
import com.liliflora.entity.Payment;
import com.liliflora.repository.OrderRepository;
import com.liliflora.repository.OrderStatusRepository;
import com.liliflora.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void payment(Long orderId) {
        log.info("PaymentService.payment()");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found")
        );

        Payment payment = new Payment(order.getOrderId());
        paymentRepository.save(payment);

        OrderStatus orderStatus = orderStatusRepository.findById(order.getOrderStatus().getOrderStatusId())
                .orElseThrow(() -> new NotFoundException("OrderStatus not found"));

        orderStatus.updatePayment();
    }
}
