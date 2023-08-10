package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class User {

    @PositiveOrZero
    private long id;

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;

}
