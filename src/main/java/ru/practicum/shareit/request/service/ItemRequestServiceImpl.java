package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.map.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoItems;
import ru.practicum.shareit.request.map.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequest addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));
        ItemRequest itemRequest = new ItemRequest(
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now()
        );
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDtoItems> getRequestsByUserId(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequestor_Id(userId, Sort.by("created").descending());
        Map<Long, ItemRequest> itemRequestMap = itemRequests.stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        List<Item> requestedItems = itemRepository.findByRequest_IdIn(itemRequestMap.keySet());

        List<ItemRequestDtoItems> itemRequestDtoItemsList = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> itemDtoList = requestedItems.stream()
                    .filter(item -> item.getRequest().getId() == itemRequest.getId())
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            ItemRequestDtoItems itemRequestDtoItems = new ItemRequestDtoItems(
                    itemRequest.getId(),
                    itemRequest.getDescription(),
                    itemRequest.getCreated(),
                    itemDtoList
            );
            itemRequestDtoItemsList.add(itemRequestDtoItems);
        }

        return itemRequestDtoItemsList;
    }

    @Override
    public List<ItemRequestDtoItems> getAllRequests(Long userId, Integer from, Integer size) {

        List<ItemRequest> itemRequestPage;

        if (from == null && size == null) {
            itemRequestPage = itemRequestRepository
                    .findByRequestor_IdNot(userId, Sort.by("created").descending());

        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());
            itemRequestPage = itemRequestRepository
                    .findByRequestor_IdNot(userId, pageable);
        }

        Map<Long, ItemRequest> itemRequestMap = itemRequestPage.stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        List<Item> requestedItems = itemRepository.findByRequest_IdIn(itemRequestMap.keySet());

        List<ItemRequestDtoItems> itemRequestDtoItemsList = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequestPage) {
            List<ItemDto> itemDtoList = requestedItems.stream()
                    .filter(item -> item.getRequest().getId() == itemRequest.getId())
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            ItemRequestDtoItems itemRequestDtoItems = new ItemRequestDtoItems(
                    itemRequest.getId(),
                    itemRequest.getDescription(),
                    itemRequest.getCreated(),
                    itemDtoList
            );
            itemRequestDtoItemsList.add(itemRequestDtoItems);
        }
        return itemRequestDtoItemsList;
    }

    @Override
    public ItemRequestDtoItems getRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Request with id="
                        + requestId + " not found.\"}"));

        List<ItemDto> itemDtoList = itemRepository.findByRequest_Id(requestId)
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        return ItemRequestMapper.toItemDto(itemRequest, itemDtoList);
    }
}
