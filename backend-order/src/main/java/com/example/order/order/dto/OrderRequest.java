package com.example.order.order.dto;

import jakarta.validation.constraints.NotNull;

public record OrderRequest(@NotNull Long itemId) {
}
