package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */

@Data
@RequiredArgsConstructor
public class Item {

    @PositiveOrZero
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private User owner;

    private ItemRequest request;

    public boolean isAvailable() {
        return available;
    }

    public Item(String name, String description, Boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
