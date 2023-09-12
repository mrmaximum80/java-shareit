package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor

public class User {

    @PositiveOrZero
    private Long id;

    @NotBlank
    @NonNull
    private String name;

    @Email
    @NotBlank
    @NonNull
    private String email;

}
