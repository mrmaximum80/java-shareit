package ru.practicum.shareit.request.map;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoItems;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDtoItems toItemDto(ItemRequest itemRequest, List<ItemDto> itemDtoList) {

        return new ItemRequestDtoItems(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemDtoList
        );
    }


}
