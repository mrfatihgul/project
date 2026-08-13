package com.example.order.order.repository;

import java.util.List;

import com.example.order.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUsernameOrderByCreatedAtDesc(String username);
}
