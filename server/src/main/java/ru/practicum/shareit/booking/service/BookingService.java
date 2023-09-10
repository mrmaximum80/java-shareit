package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    Booking addBooking(Long userId, BookingDto bookingDto);

    Booking approveBooking(Long userId, long bookingId, String approved);

    Booking getBooking(Long userId, long bookingId);

    List<Booking> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<Booking> getOwnerBookings(Long ownerId, String state, Integer from, Integer size);

}
