package com.liliflora.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RedisHash("OrderPaying")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaying {

    @Id
    private String orderPayingId;

    private long productId;
    private int quantity;
    private String regDate;

    public OrderPaying(String orderPayingId, long productId, int quantity) {
        this.orderPayingId = orderPayingId;
        this.productId = productId;
        this.quantity = quantity;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.regDate = now.format(formatter);
    }

}
