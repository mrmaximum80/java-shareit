package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long userId, Sort sort);

    List<Booking> findByBooker_Id(Long userId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfter(Long userId, LocalDateTime date, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long userId, LocalDateTime date, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime date1, LocalDateTime date2,
                                                               Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long userId, Status status);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findOwnerBookings(Long ownerId);

    List<Booking> findByItem_Id(Long itemId, Sort sort);

    List<Booking> findByItem_IdAndStatus(long id, Status status, Sort sort);

    List<Booking> findByItem_IdInAndStatus(Set<Long> ids, Status status, Sort start);

    List<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long userId, long itemId, LocalDateTime date);

}

