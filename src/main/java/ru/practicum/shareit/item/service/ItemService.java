package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addNewItem(Long userId, ItemDto itemDto);

    Item editItem(Long userId, ItemDto itemDto, long itemId);

    Item getItem(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> findItems(String text);
}
