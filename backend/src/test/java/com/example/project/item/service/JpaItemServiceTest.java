package com.example.project.item.service;

import com.example.project.item.entity.Item;
import com.example.project.item.entity.ItemDetail;
import com.example.project.item.repository.ItemDetailRepository;
import com.example.project.item.repository.ItemRepository;
import com.example.project.user.entity.AppUser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class JpaItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemDetailRepository itemDetailRepository;

    @InjectMocks
    private JpaItemService jpaItemService;


    @Test
    public void findByOwner_ShouldReturnItems_WhenOwnerExists() {

        AppUser owner = new AppUser();

        Item item1 = new Item();
        Item item2 = new Item();

        List<Item> expectedItems = List.of(item1, item2);

        when(itemRepository.findByOwner(owner))
                .thenReturn(expectedItems);

        List<Item> result = jpaItemService.findByOwner(owner);

        assertEquals(expectedItems, result);

        verify(itemRepository).findByOwner(owner);
    }


    @Test
    public void findByIdAndOwner_ShouldReturnItem_WhenItemExists() {

        AppUser owner = new AppUser();
        Item item = new Item();

        when(itemRepository.findByIdAndOwner(1L, owner))
                .thenReturn(Optional.of(item));

        Item result = jpaItemService.findByIdAndOwner(1L, owner);

        assertEquals(item, result);

        verify(itemRepository).findByIdAndOwner(1L, owner);
    }


    @Test
    public void findByIdAndOwner_ShouldThrowException_WhenItemDoesNotExist() {

        AppUser owner = new AppUser();

        when(itemRepository.findByIdAndOwner(1L, owner))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> jpaItemService.findByIdAndOwner(1L, owner)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        assertEquals(
                "Item bulunamadı",
                exception.getReason()
        );
    }


    @Test
    public void create_ShouldSaveItemAndDetails_WhenDetailsExist() {

        Item item = new Item();
        ItemDetail itemDetail = new ItemDetail();
        ItemDetail savedItemDetail = new ItemDetail();

        item.setDetails(itemDetail);

        when(itemDetailRepository.save(itemDetail))
                .thenReturn(savedItemDetail);

        when(itemRepository.save(item))
                .thenReturn(item);

        Item result = jpaItemService.create(item);

        assertEquals(item, result);
        assertEquals(savedItemDetail, item.getDetails());

        verify(itemDetailRepository).save(itemDetail);
        verify(itemRepository).save(item);
    }


    @Test
    public void create_ShouldSaveOnlyItem_WhenDetailsDoNotExist() {

        Item item = new Item();

        when(itemRepository.save(item))
                .thenReturn(item);

        Item result = jpaItemService.create(item);

        assertEquals(item, result);

        verify(itemRepository).save(item);

        verify(itemDetailRepository, never())
                .save(any(ItemDetail.class));
    }
}