package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking editBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @PathVariable long bookingId,
                         @RequestParam(name = "approved") String approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }

}
