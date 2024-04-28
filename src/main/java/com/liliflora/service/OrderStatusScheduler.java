package com.liliflora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderService orderService;

    // 매일 자정마다 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateOrderStatus() {
        orderService.updateOrderStatus();
    }
}
