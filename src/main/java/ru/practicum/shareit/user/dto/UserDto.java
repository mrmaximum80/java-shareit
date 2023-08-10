package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;


}
