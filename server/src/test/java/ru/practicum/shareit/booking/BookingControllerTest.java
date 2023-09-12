package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    static BookingDto bookingDto;
    static User user;
    static Item item;
    static Booking booking;

    @BeforeAll
    static void before() {
        bookingDto = new BookingDto(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 1L);
        user = new User(1L, "user", "user@mail");
        item = new Item("Item name", "Item description",
                true, user, null);
        item.setId(1L);
        booking = new Booking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                Status.APPROVED);
        booking.setId(1L);
    }

    @Test
    void addBookingTest() throws Exception {

        Booking booking = new Booking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                Status.WAITING);

        booking.setId(1L);

        Mockito.when(bookingService.addBooking(1L, bookingDto))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(booking.getBooker().getName())));
    }

    @Test
    void approveBookingTest() throws Exception {

        Mockito.when(bookingService.approveBooking(1L, 1L, "true"))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBookingByIdTest() throws Exception {

        Mockito.when(bookingService.getBooking(1L, 1L))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getUserBookingsTest() throws Exception {

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Mockito.when(bookingService.getUserBookings(1L, "ALL", 0, 20))
                .thenReturn(bookings);

        mvc.perform(get("/bookings?state=ALL&from=0&size=20", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is("APPROVED")));
    }

    @Test
    void getOwnerBookingsTest() throws Exception {

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Mockito.when(bookingService.getOwnerBookings(1L, "ALL", 0, 20))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=20", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.name", is(booking.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is("APPROVED")));
    }
}
