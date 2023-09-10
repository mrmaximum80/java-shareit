package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemDtoComment {

    private Long id;

    @NotBlank
    private String text;

    @NotBlank
    private String authorName;

    @PastOrPresent
    private LocalDateTime created;

}
