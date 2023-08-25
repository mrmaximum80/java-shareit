package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @PositiveOrZero
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NonNull
    @Column(name = "start_date")
    private LocalDateTime start;

    @NotNull
    @NonNull
    @Column(name = "end_date")
    private LocalDateTime end;

    @NotNull
    @NonNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    @NonNull
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @NotNull
    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;
}
