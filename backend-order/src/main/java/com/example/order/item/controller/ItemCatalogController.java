package com.example.order.item.controller;

import java.util.List;

import com.example.order.item.dto.ItemView;
import com.example.order.item.service.ItemCatalogService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemCatalogController {

    private final ItemCatalogService itemCatalogService;

    public ItemCatalogController(ItemCatalogService itemCatalogService) {
        this.itemCatalogService = itemCatalogService;
    }

    @GetMapping
    public List<ItemView> findAll(@AuthenticationPrincipal Jwt jwt) {
        return itemCatalogService.findAll(jwt.getTokenValue());
    }

    @GetMapping("/{id}")
    public ItemView findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return itemCatalogService.findById(id, jwt.getTokenValue());
    }
}
