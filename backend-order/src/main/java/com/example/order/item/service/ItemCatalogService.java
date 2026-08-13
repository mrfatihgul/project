package com.example.order.item.service;

import java.util.List;

import com.example.order.item.client.ItemCatalogClient;
import com.example.order.item.dto.ItemView;
import org.springframework.stereotype.Service;

@Service
public class ItemCatalogService {

    private final ItemCatalogClient itemCatalogClient;

    public ItemCatalogService(ItemCatalogClient itemCatalogClient) {
        this.itemCatalogClient = itemCatalogClient;
    }

    public List<ItemView> findAll(String accessToken) {
        return itemCatalogClient.findAll(accessToken);
    }

    public ItemView findById(Long itemId, String accessToken) {
        return itemCatalogClient.findById(itemId, accessToken);
    }
}
