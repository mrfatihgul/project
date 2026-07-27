package com.example.project.item.service;
import com.example.project.item.entity.Item;
import com.example.project.user.entity.AppUser;

import java.util.List;

public interface ItemService {
    List<Item> findAll();

    Item findById(Long id);

    List<Item> findByOwner(AppUser owner);

    Item findByIdAndOwner(Long id, AppUser owner);

    Item create(Item item);

    Item update(Long id, Item item);
}
