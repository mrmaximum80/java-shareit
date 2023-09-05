package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class ItemRequestDtoItems {

    @NotNull
    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private List<ItemDto> items;
}
