package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.ItemBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDtoBooking {

    @PositiveOrZero
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private User owner;

    private ItemBooking lastBooking;

    private ItemBooking nextBooking;

    private List<ItemDtoComment> comments;

    @Transient
    private ItemRequest request;

    public boolean isAvailable() {
        return available;
    }

    public ItemDtoBooking(String name, String description, Boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
