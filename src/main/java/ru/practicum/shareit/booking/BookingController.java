package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
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
    public Booking approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
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
                                         @RequestParam(name = "state", defaultValue = "ALL") String state,
                                         @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                         @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestParam(name = "state", defaultValue = "ALL") String state,
                                          @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                          @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        return bookingService.getOwnerBookings(ownerId, state, from, size);
    }

}
