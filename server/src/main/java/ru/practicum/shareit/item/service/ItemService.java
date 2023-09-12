package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoComment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, ItemDto itemDto, long itemId);

    ItemDtoBooking getItem(long itemId, long userId);

    List<ItemDtoBooking> getItemsByUserId(long userId);

    List<Item> findItems(String text);

    ItemDtoComment addComment(Long userId, Long itemId, CommentDto commentDto);
}
