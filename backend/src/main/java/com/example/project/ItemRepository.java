package com.example.project;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(AppUser owner);

    Optional<Item> findByIdAndOwner(Long id, AppUser owner);
}