package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemDtoComment {

    @NotNull
    private long id;

    @NotBlank
    private String text;

    @NotBlank
    private String authorName;

    @PastOrPresent
    private LocalDateTime created;

}
