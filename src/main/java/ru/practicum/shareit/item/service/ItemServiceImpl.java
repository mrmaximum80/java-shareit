package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.map.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item addNewItem(Long userId, ItemDto itemDto) {
        if (userRepository.getUserById(userId).isPresent()) {
            Item item = ItemMapper.toItem(userRepository.getUserById(userId).get(), itemDto);
            return itemRepository.save(item);
        } else {
            throw new NotFoundException("User with id=" + userId + " not found!");
        }

    }

    @Override
    public Item editItem(Long userId, ItemDto itemDto, long itemId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Item with id=" + itemId + " not found.\"}"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("{\"message\": \"Item with id=" + itemId
                    + " does not belong to user with id=" + userId + ".\"}");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    @Override
    public Item getItem(long itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Item with id=" + itemId + " not found.\"}"));
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return itemRepository.getItemsByUserId(userId);
    }

    @Override
    public List<Item> findItems(String text) {
        return itemRepository.findItem(text);
    }

}
