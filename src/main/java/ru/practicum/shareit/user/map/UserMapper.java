package ru.practicum.shareit.user.map;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(UserDto userDto, long id) {
        return new User(
                id,
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
