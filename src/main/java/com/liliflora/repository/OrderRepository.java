package com.liliflora.repository;

import com.liliflora.entity.Order;
import com.liliflora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByUserOrderByPurchaseDateDesc(User user);

    List<Order> findAllByUserOrderByPurchaseDateDesc(User user);
}
