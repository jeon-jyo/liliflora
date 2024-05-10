package com.liliflora.repository;

import com.liliflora.entity.Stock;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;

@RedisHash("Stock")
public interface StockRepository extends CrudRepository<Stock, Long> {
}
