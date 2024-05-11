package com.liliflora.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Stock")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    private Long stockId;

    private int quantity;


    public void decreaseQuantity(int quantity) throws Exception {
        this.quantity -= quantity;

        if (this.quantity < 0) {
            throw new Exception("SOLD_OUT");
        }
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }
}
