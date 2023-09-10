package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoItems;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoItems> getRequestsByUserId(Long userId);

    List<ItemRequestDtoItems> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoItems getRequestById(Long userId, Long requestId);
}
