package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {

    @PositiveOrZero
    private long id;

    @NotNull
    @PastOrPresent
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    private LocalDateTime end;

    @NotNull
    private Item item;

    private User booker;

    private Status status;
}
