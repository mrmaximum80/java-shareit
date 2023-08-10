package ru.practicum.shareit.user.map;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto, long id) {
        return new User(
                id,
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
