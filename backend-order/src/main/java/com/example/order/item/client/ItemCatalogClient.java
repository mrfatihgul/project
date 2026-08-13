package com.example.order.item.client;

import java.util.List;

import com.example.order.item.dto.ItemView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ItemCatalogClient {

    private final RestClient restClient;

    public ItemCatalogClient(@Value("${app.items.base-url}") String itemsBaseUrl) {
        this.restClient = RestClient.builder().baseUrl(itemsBaseUrl).build();
    }

    public List<ItemView> findAll(String accessToken) {
        try {
            List<ItemView> items = restClient.get()
                    .uri("/api/items/catalog")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            return items != null ? items : List.of();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Item servisine ulaşılamadı",
                    e
            );
        }
    }

    public ItemView findById(Long itemId, String accessToken) {
        try {
            return restClient.get()
                    .uri("/api/items/catalog/{id}", itemId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(ItemView.class);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item bulunamadı: " + itemId,
                    e
            );
        }
    }
}
