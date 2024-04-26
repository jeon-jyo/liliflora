package com.liliflora.repository;

import com.liliflora.entity.Order;
import com.liliflora.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByUserOrderByPurchaseDateDesc(@Param("user") User user);
}
