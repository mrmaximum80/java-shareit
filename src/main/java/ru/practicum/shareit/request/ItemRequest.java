package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ItemRequest {

    private long id;

    @NotBlank
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    @PastOrPresent
    private LocalDateTime created;

}
