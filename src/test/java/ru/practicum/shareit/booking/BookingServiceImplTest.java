package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;


    Item item;
    ItemDto itemDto;
    User owner;
    User booker;
    UserDto userDto;
    Booking booking;
    BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto("owner", "owner@mail");
        owner = userService.createUser(userDto);
        userDto = new UserDto("booker", "booker@mail");
        booker = userService.createUser(userDto);
        itemDto = new ItemDto("Item name", "Item description", true, null);
        itemDto = itemService.addNewItem(owner.getId(), itemDto);
        bookingDto = new BookingDto(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), itemDto.getId());

    }

    @Test
    void addBookingTest() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        assertNotNull(booking.getId());
        assertNotNull(booking.getStart());
        assertNotNull(booking.getEnd());
        assertEquals(booking.getStatus(), Status.WAITING);
    }

    @Test
    void addBookingTestEndBeforeStart() {
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        final NotAvailableException exception = assertThrows(

                NotAvailableException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        booking = bookingService.addBooking(booker.getId(), bookingDto);
                    }
                });
    }

    @Test
    void addBookingTestNotAvailable() {
        itemDto.setAvailable(false);
        itemService.editItem(owner.getId(), itemDto, itemDto.getId());

        final NotAvailableException exception = assertThrows(

                NotAvailableException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        booking = bookingService.addBooking(booker.getId(), bookingDto);
                    }
                });

        itemDto.setAvailable(true);
    }

    @Test
    void addBookingTestOwner() {
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        booking = bookingService.addBooking(owner.getId(), bookingDto);
                    }
                });
    }

    @Test
    void approveBookingTest() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        assertNotNull(booking.getId());
        assertNotNull(booking.getStart());
        assertNotNull(booking.getEnd());
        assertEquals(booking.getStatus(), Status.WAITING);

        booking = bookingService.approveBooking(owner.getId(), booking.getId(), "true");
        assertEquals(booking.getStatus(), Status.APPROVED);
    }

    @Test
    void approveBookingNotWaiting() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        booking = bookingService.approveBooking(owner.getId(), booking.getId(), "true");

        final NotAvailableException exception = assertThrows(
                NotAvailableException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        booking = bookingService.approveBooking(owner.getId(), booking.getId(), "true");
                    }
                });
    }

    @Test
    void approveBookingNotOwner() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        booking = bookingService.approveBooking(100L, booking.getId(), "true");
                    }
                });
    }

    @Test
    void approveBookingBadApprove() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        final NotAvailableException exception = assertThrows(
                NotAvailableException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        booking = bookingService.approveBooking(owner.getId(), booking.getId(), "tru");
                    }
                });
    }

    @Test
    void getBookingTest() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        booking = bookingService.getBooking(booker.getId(), booking.getId());

        assertNotNull(booking.getId());
        assertNotNull(booking.getStart());
        assertNotNull(booking.getEnd());
        assertEquals(booking.getStatus(), Status.WAITING);
    }

    @Test
    void getBookingTestWrongUser() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        booking = bookingService.getBooking(100L, booking.getId());
                    }
                });

    }

    @Test
    void getUserBookingsTestAll() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "ALL", 0, 20);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertNotNull(bookings.get(0).getEnd());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getUserBookingsTestAllNullebleFromAndSize() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "ALL", null, null);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertNotNull(bookings.get(0).getEnd());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getUserBookingsTestPast() {
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "PAST", 0, 20);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertTrue(bookings.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void getUserBookingsTestFuture() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "FUTURE", 0, 20);
        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertTrue(bookings.get(0).getStart().isAfter(LocalDateTime.now()));
        ;
    }

    @Test
    void getUserBookingsTestCurrent() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "CURRENT", 0, 20);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertTrue(bookings.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookings.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void getUserBookingsTestWaiting() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "WAITING", 0, 20);
        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getUserBookingsTestRejected() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        booking = bookingService.approveBooking(owner.getId(), booking.getId(), "false");

        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "REJECTED", 0, 20);
        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    void getOwnerBookingsTestAll() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "ALL", 0, 20);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertNotNull(bookings.get(0).getEnd());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getOwnerBookingsTestAllNullebleFromAndSize() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "ALL", null, null);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertNotNull(bookings.get(0).getEnd());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getOwnerBookingsTestPast() {
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "PAST", 0, 20);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertTrue(bookings.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void getOwnerBookingsTestFuture() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "FUTURE", 0, 20);
        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertTrue(bookings.get(0).getStart().isAfter(LocalDateTime.now()));
        ;
    }

    @Test
    void getOwnerBookingsTestCurrent() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        booking = bookingService.addBooking(booker.getId(), bookingDto);

        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "CURRENT", 0, 20);

        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertTrue(bookings.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookings.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void getOwnerBookingsTestWaiting() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "WAITING", 0, 20);
        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getOwnerBookingsTestRejected() {
        booking = bookingService.addBooking(booker.getId(), bookingDto);
        booking = bookingService.approveBooking(owner.getId(), booking.getId(), "false");

        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "REJECTED", 0, 20);
        assertEquals(bookings.size(), 1);
        assertNotNull(bookings.get(0).getId());
        assertNotNull(bookings.get(0).getStart());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    void getOwnerBookingsTestWrongState() {

        final UnknownStateException exception = assertThrows(

                UnknownStateException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        List<Booking> bookings = bookingService
                                .getOwnerBookings(owner.getId(), "XYZ", 0, 20);
                    }
                });
    }


}
