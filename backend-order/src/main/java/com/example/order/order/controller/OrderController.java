package com.example.order.order.controller;

import java.util.List;

import com.example.order.order.dto.OrderRequest;
import com.example.order.order.dto.OrderResponse;
import com.example.order.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return OrderResponse.from(
                orderService.create(
                        jwt.getSubject(),
                        request.itemId(),
                        jwt.getTokenValue()
                )
        );
    }

    @GetMapping
    public List<OrderResponse> findMine(@AuthenticationPrincipal Jwt jwt) {
        return orderService.findByUsername(jwt.getSubject()).stream()
                .map(OrderResponse::from)
                .toList();
    }
}
