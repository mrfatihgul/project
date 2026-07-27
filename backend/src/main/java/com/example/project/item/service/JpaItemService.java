package com.example.project.item.service;
import com.example.project.item.entity.Item;
import com.example.project.item.entity.ItemDetail;
import com.example.project.item.repository.ItemDetailRepository;
import com.example.project.item.repository.ItemRepository;
import com.example.project.user.entity.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class JpaItemService implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemDetailRepository itemDetailRepository;

    public JpaItemService(
            ItemRepository itemRepository,
            ItemDetailRepository itemDetailRepository
    ) {
        this.itemRepository = itemRepository;
        this.itemDetailRepository = itemDetailRepository;
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item bulunamadı: " + id
                ));
    }

    @Override
    public List<Item> findByOwner(AppUser owner) {
        return itemRepository.findByOwner(owner);
    }

    @Override
    public Item findByIdAndOwner(Long id, AppUser owner) {
        return itemRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item bulunamadı"
                ));
    }

    @Override
    public Item create(Item item) {
        if (item.getDetails() != null) {
            ItemDetail savedDetail =
                    itemDetailRepository.save(item.getDetails());

            item.setDetails(savedDetail);
        }

        return itemRepository.save(item);
    }

    @Override
    public Item update(Long id, Item item) {
        Item existingItem = findById(id);

        existingItem.setName(item.getName());

        if (item.getDetails() != null) {
            ItemDetail savedDetail =
                    itemDetailRepository.save(item.getDetails());

            existingItem.setDetails(savedDetail);
        }

        return itemRepository.save(existingItem);
    }
}
