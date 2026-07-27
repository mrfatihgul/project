package com.example.project.item.controller;

import java.util.List;

import com.example.project.item.entity.Item;
import com.example.project.item.repository.ItemDetailRepository;
import com.example.project.item.service.ItemService;
import com.example.project.user.entity.AppUser;
import com.example.project.user.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemDetailRepository itemDetailRepository;
    private final AppUserRepository userRepository;

    public ItemController(ItemService itemService, ItemDetailRepository itemDetailRepository, AppUserRepository userRepository) {
        this.itemService = itemService;
        this.itemDetailRepository = itemDetailRepository;
        this.userRepository = userRepository;
    }

    private AppUser currentUser(Jwt jwt) {
        return userRepository.findByUsername(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping
    public List<Item> findAll(@AuthenticationPrincipal Jwt jwt) {
        AppUser user = currentUser(jwt);
        return itemService.findByOwner(user);
    }

    @GetMapping("/{id}")
    public Item findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AppUser user = currentUser(jwt);
        return itemService.findByIdAndOwner(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(
            @RequestBody Item item,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AppUser user = currentUser(jwt);
        item.setId(null);
        item.setOwner(user);

        if (item.getDetails() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Item details zorunludur"
            );
        }

        itemDetailRepository.save(item.getDetails());
        return itemService.create(item);
    }

    @PutMapping("/{id}")
    public Item update(
            @PathVariable Long id,
            @RequestBody Item request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AppUser user = currentUser(jwt);
        Item item = itemService.findByIdAndOwner(id, user);

        item.setName(request.getName());

        if (request.getDetails() != null) {
            if (item.getDetails() != null) {
                item.getDetails().setType(request.getDetails().getType());
                item.getDetails().setDescription(request.getDetails().getDescription());
                item.getDetails().setMaterial(request.getDetails().getMaterial());
                itemDetailRepository.save(item.getDetails());
            } else {
                item.setDetails(itemDetailRepository.save(request.getDetails()));
            }
        }

        return itemService.create(item);
    }
}