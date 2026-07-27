package com.example.project.item.repository;

import java.util.List;
import java.util.Optional;

import com.example.project.item.entity.Item;
import com.example.project.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(AppUser owner);

    Optional<Item> findByIdAndOwner(Long id, AppUser owner);
}