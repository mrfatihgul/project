package com.example.order.order.service;

import java.util.List;

import com.example.order.order.entity.Order;

public interface OrderService {

    Order create(String username, Long itemId, String accessToken);

    List<Order> findByUsername(String username);
}
