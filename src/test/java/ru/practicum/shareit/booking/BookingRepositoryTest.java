package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        owner = new User("owner", "owner@mail");
        booker = new User("booker", "booker@mail");
        item = new Item("Item name", "Item description", true, owner, null);
        booking = new Booking(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, Status.WAITING);
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(item);
        entityManager.persist(booking);
        entityManager.flush();
    }

    @Test
    void findOwnerBookingsTest() {
        List<Booking> foundBookings = bookingRepository.findOwnerBookings(owner.getId());

        assertEquals(1, foundBookings.size());
        assertEquals(booking.getId(), foundBookings.get(0).getId());
    }

    @Test
    void findByBooker_IdAndStatusTest() {
        List<Booking> foundBookings = bookingRepository.findByBooker_IdAndStatus(booker.getId(), Status.WAITING);

        assertEquals(1, foundBookings.size());
        assertEquals(booking.getId(), foundBookings.get(0).getId());
    }

    @Test
    void findByBooker_IdAndStartIsBeforeAndEndIsAfterTest() {
        List<Booking> foundBookings = bookingRepository
                .findByBooker_IdAndStartIsBeforeAndEndIsAfter(booker.getId(),
                        LocalDateTime.now().plusHours(36),
                        LocalDateTime.now().plusHours(36),
                        Sort.unsorted());

        assertEquals(1, foundBookings.size());
        assertEquals(booking.getId(), foundBookings.get(0).getId());
    }
}
