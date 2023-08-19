package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private long itemId = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(++itemId);
        items.put(itemId, item);
        log.info("Item with id = {} added.", itemId);
        return item;
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            log.info("Item with id={} not found", itemId);
        } else {
            log.info("Item with id={} found", itemId);
        }
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item i : items.values()) {
            if (i.getOwner().getId() == userId) {
                userItems.add(i);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> findItem(String text) {
        List<Item> foundItems = new ArrayList<>();
        if (!text.isEmpty()) {
            text = text.toLowerCase();
            for (Item i : items.values()) {
                if (i.getName().toLowerCase().contains(text) || i.getDescription().toLowerCase().contains(text)
                        && i.isAvailable()) {
                    foundItems.add(i);
                }
            }
        }
        return foundItems;
    }
}
