package com.example.order.order.service;

import java.util.List;

import com.example.order.item.service.ItemCatalogService;
import com.example.order.order.entity.Order;
import com.example.order.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class JpaOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemCatalogService itemCatalogService;

    public JpaOrderService(
            OrderRepository orderRepository,
            ItemCatalogService itemCatalogService
    ) {
        this.orderRepository = orderRepository;
        this.itemCatalogService = itemCatalogService;
    }

    @Override
    public Order create(String username, Long itemId, String accessToken) {
        itemCatalogService.findById(itemId, accessToken);

        Order order = new Order();
        order.setUsername(username);
        order.setItemId(itemId);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findByUsername(String username) {
        return orderRepository.findByUsernameOrderByCreatedAtDesc(username);
    }
}
