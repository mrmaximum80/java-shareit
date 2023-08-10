package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@RequiredArgsConstructor
public class ItemRequest {

    private long id;

    @NotNull
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    @PastOrPresent
    private LocalDateTime created;

}
