package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @PositiveOrZero
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false, name = "item_id")
    private Item item;


    @ManyToOne
    @JoinColumn(nullable = false, name = "author_id")
    private User author;

    @NotNull
    @Column(name = "created")
    private LocalDateTime created;

    public Comment(String text, Item item, User author, LocalDateTime created) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }
}
