package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoComment;
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
class ItemServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    UserDto userDto;
    User user;
    long userId;
    ItemDto itemDto;
    long itemId;

    @BeforeEach
    void before() {
        userDto = new UserDto("user", "user@mail");
        user = userService.createUser(userDto);
        userId = user.getId();

        itemDto = new ItemDto("Item name", "Item description", true, null);
        itemDto = itemService.addNewItem(userId, itemDto);
        itemId = itemDto.getId();
    }

    @Test
    void addNewItemTest() {

        assertNotNull(itemDto.getId());
        assertEquals(itemDto.getName(), "Item name");
        assertEquals(itemDto.getDescription(), "Item description");
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void editItemTest() {

        itemDto.setName("Item name2");
        itemDto.setDescription("Item description2");
        itemDto = itemService.editItem(userId, itemDto, itemId);

        assertEquals("Item name2", itemDto.getName());
        assertEquals("Item description2", itemDto.getDescription());
    }

    @Test
    void getItemTest() {

        UserDto bookerDto = new UserDto("booker", "booker@mail");
        User booker = userService.createUser(bookerDto);
        long bookerId = booker.getId();

        BookingDto bookingDto = new BookingDto(LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30), itemId);

        Booking booking = bookingService.addBooking(bookerId, bookingDto);
        long bookingId = booking.getId();
        booking = bookingService.approveBooking(userId, bookingId, "true");

        BookingDto futureBookingDto = new BookingDto(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), itemId);
        Booking futureBooking = bookingService.addBooking(bookerId, futureBookingDto);
        long futureBookingId = futureBooking.getId();
        futureBooking = bookingService.approveBooking(userId, futureBookingId, "true");

        CommentDto commentDto = new CommentDto("Comment");
        ItemDtoComment comment = itemService.addComment(bookerId, itemId, commentDto);

        ItemDtoBooking itemDtoBooking = itemService.getItem(itemId, userId);

        assertNotNull(itemDtoBooking.getId());
        assertEquals(itemDtoBooking.getName(), "Item name");
        assertEquals(itemDtoBooking.getDescription(), "Item description");
    }

    @Test
    void getItemsByUserIdTest() {

        UserDto bookerDto = new UserDto("booker", "booker@mail");
        User booker = userService.createUser(bookerDto);
        long bookerId = booker.getId();

        BookingDto bookingDto = new BookingDto(LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30), itemId);

        Booking booking = bookingService.addBooking(bookerId, bookingDto);
        long bookingId = booking.getId();
        booking = bookingService.approveBooking(userId, bookingId, "true");

        CommentDto commentDto = new CommentDto("Comment");
        ItemDtoComment comment = itemService.addComment(bookerId, itemId, commentDto);

        List<ItemDtoBooking> itemDtoBookings = itemService.getItemsByUserId(userId);

        assertEquals(itemDtoBookings.size(), 1);
        assertEquals(itemDtoBookings.get(0).getName(), "Item name");
    }

    @Test
    void findItemsTest() {
        List<Item> items = itemService.findItems("deSc");

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "Item name");

        items = itemService.findItems(" ");
        assertEquals(items.size(), 0);
    }

    @Test
    void addCommentTest() {
        UserDto bookerDto = new UserDto("booker", "booker@mail");
        User booker = userService.createUser(bookerDto);
        long bookerId = booker.getId();

        BookingDto bookingDto = new BookingDto(LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30), itemId);

        Booking booking = bookingService.addBooking(bookerId, bookingDto);
        long bookingId = booking.getId();
        booking = bookingService.approveBooking(userId, bookingId, "true");

        CommentDto commentDto = new CommentDto("Comment");

        ItemDtoComment comment = itemService.addComment(bookerId, itemId, commentDto);

        assertEquals(comment.getText(), "Comment");
        assertEquals(comment.getAuthorName(), "booker");
        assertNotNull(comment.getCreated());
    }
}
