package com.example.order.order.dto;

import java.time.Instant;

import com.example.order.order.entity.Order;

public record OrderResponse(
        Long id,
        String username,
        Long itemId,
        Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUsername(),
                order.getItemId(),
                order.getCreatedAt()
        );
    }
}
