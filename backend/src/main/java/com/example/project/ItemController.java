package com.example.project;

import java.util.List;

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

    private final ItemRepository itemRepository;
    private final AppUserRepository userRepository;

    public ItemController(
            ItemRepository itemRepository,
            AppUserRepository userRepository
    ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    private AppUser currentUser(Jwt jwt) {
        return userRepository.findByUsername(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping
    public List<Item> findAll(@AuthenticationPrincipal Jwt jwt) {
        AppUser user = currentUser(jwt);
        return itemRepository.findByOwner(user);
    }

    @GetMapping("/{id}")
    public Item findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AppUser user = currentUser(jwt);
        return itemRepository.findByIdAndOwner(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
        return itemRepository.save(item);
    }

    @PutMapping("/{id}")
    public Item update(
            @PathVariable Long id,
            @RequestBody Item request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AppUser user = currentUser(jwt);
        Item item = itemRepository.findByIdAndOwner(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        item.setName(request.getName());
        return itemRepository.save(item);
    }
}