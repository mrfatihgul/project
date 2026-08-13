package com.example.project.item.service;
import com.example.project.item.entity.Item;
import com.example.project.user.entity.AppUser;

import java.util.List;

public interface ItemService {
    List<Item> findByOwner(AppUser owner);

    List<Item> findAll();

    Item findByIdAndOwner(Long id, AppUser owner);

    Item findById(Long id);

    Item create(Item item);

    void deleteByIdAndOwner(Long id, AppUser owner);
}
