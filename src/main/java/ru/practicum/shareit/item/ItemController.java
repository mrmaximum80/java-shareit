package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @Valid @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto itemDto, @PathVariable long itemId) {
        return itemService.editItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<Item> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<Item> findItems(@RequestParam(name = "text") String text) {
        return itemService.findItems(text);
    }

}
