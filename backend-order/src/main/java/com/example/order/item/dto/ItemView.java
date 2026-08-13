package com.example.order.item.dto;

public record ItemView(
        Long id,
        String name,
        ItemDetailView details
) {
}
