package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.map.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, ItemRepository itemRepository,
                              BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking addBooking(Long userId, BookingDto bookingDto) {
        if (!bookingDto.startBeforeEnd()) {
            throw new NotAvailableException("{\"message\": \"End date can not be before start date!\"}");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));
        long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Item with id=" + itemId + " not found.\"}"));
        if (!item.isAvailable()) {
            throw new NotAvailableException("{\"message\": \"Item with id=" + itemId + "is not available.\"}");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("{\"message\": \"Owner of item can not create booking.\"}");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
        log.info("Booking for item with id = {} is added.", itemId);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Long userId, long bookingId, String approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Booking with id=" + bookingId + " not found.\"}"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new NotAvailableException("{\"message\": \"Booking with id=" + bookingId + " is already approved.\"}");
        }
        long itemOwnerId = booking.getItem().getOwner().getId();
        if (userId != itemOwnerId) {
            throw new NotFoundException("{\"message\": \"Wrong owner id!\"}");
        }
        if (approved.equalsIgnoreCase("true")) {
            booking.setStatus(Status.APPROVED);
        } else if (approved.equalsIgnoreCase("false")) {
            booking.setStatus(Status.REJECTED);
        } else {
            throw new NotAvailableException("{\"message\": \"Wrong approved status!\"}");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Booking with id=" + bookingId + " not found.\"}"));
        long bookerId = booking.getBooker().getId();
        long itemOwnerId = booking.getItem().getOwner().getId();
        if (userId != bookerId && userId != itemOwnerId) {
            throw new NotFoundException("{\"message\": \"Wrong user id!\"}");
        }
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(Long userId, String state, Integer from, Integer size) {

        State stateEnum = getState(state);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + userId + " not found.\"}"));

        List<Booking> bookings = new ArrayList<>();
        List<Booking> allBookings = new ArrayList<>();

        if (from == null && size == null) {
            allBookings = bookingRepository.findByBooker_Id(userId, Sort.by("start").descending());
        } else {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());
            allBookings = bookingRepository.findByBooker_Id(userId, pageable);
        }

        switch (stateEnum) {
            case ALL:
                bookings = allBookings;
                break;
            case PAST:
                bookings = allBookings.stream().filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = allBookings.stream().filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = allBookings.stream().filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = allBookings.stream().filter(b -> b.getStatus().equals(Status.WAITING))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = allBookings.stream().filter(b -> b.getStatus().equals(Status.REJECTED))
                        .collect(Collectors.toList());
                break;
        }

        return bookings;
    }

    @Override
    public List<Booking> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {

        State stateEnum = getState(state);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"User with id=" + ownerId + " not found.\"}"));

        List<Booking> allBookings;
        if (from == null && size == null) {
            allBookings = bookingRepository.findOwnerBookings(ownerId);
        } else {
            allBookings = bookingRepository.findOwnerBookings(ownerId)
                    .stream().skip(from).limit(size).collect(Collectors.toList());
        }

        if (allBookings.isEmpty()) {
            throw new NotFoundException("{\"message\": \"User with id=" + ownerId + " has no any item\"}");
        }

        List<Booking> bookings = new ArrayList<>();

        switch (stateEnum) {
            case ALL:
                bookings = allBookings;
                break;
            case PAST:
                bookings = allBookings.stream().filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = allBookings.stream().filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = allBookings.stream().filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = allBookings.stream().filter(b -> b.getStatus().equals(Status.WAITING))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = allBookings.stream().filter(b -> b.getStatus().equals(Status.REJECTED))
                        .collect(Collectors.toList());
                break;
        }

        return bookings;
    }

    private State getState(String state) {
        State stateEnum = null;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            log.info("Unknown state: {}", state);
            throw new UnknownStateException("{\"error\": \"Unknown state: " + state + "\"}");
        }
        ;
        return stateEnum;
    }

}
