package com.liliflora.repository;

import com.liliflora.entity.OrderPaying;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;

@RedisHash("OrderPaying")
public interface OrderPayingRepository extends CrudRepository<OrderPaying, String> {
}
