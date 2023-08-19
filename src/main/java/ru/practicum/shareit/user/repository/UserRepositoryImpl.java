package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.map.UserMapper;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private long idCounter = 0;
    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User createUser(UserDto userDto) {
        for (User u : users.values()) {
            if (u.getEmail().equals(userDto.getEmail())) {
                log.error("Пользователь с таким email уже есть в списке");
                throw new AlreadyExistException("Пользователь с таким email/login уже есть в списке");
            }
        }

        User newUser = UserMapper.toUser(userDto, ++idCounter);
        users.put(idCounter, newUser);
        log.info("Пользователь {} добавлен", newUser.getName());
        return newUser;
    }

    @Override
    public User updateUser(UserDto userDto, long id) {
        User updatedUser = null;
        if (users.containsKey(id)) {
            for (User u : users.values()) {
                if ((u.getEmail().equals(userDto.getEmail()))
                        && (u.getId() != id)) {
                    log.error("Пользователь с таким email уже есть в списке");
                    throw new AlreadyExistException("Пользователь с таким email уже есть в списке");
                }
            }
            updatedUser = users.get(id);
            if (userDto.getName() != null && !userDto.getName().isBlank()) {
                updatedUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
                updatedUser.setEmail(userDto.getEmail());
            }
            users.put(id, updatedUser);
            log.info("Пользователь {} изменен", updatedUser.getName());
        } else {
            throw new NotFoundException("{\"message\": \"Пользователь с id=" + id + " не найден\"}");
        }
        return updatedUser;
    }

    @Override
    public void deleteUser(long id) {
        User user = users.remove(id);
        if (user == null) {
            log.info("Пользователь с id={} не найден", id);
            throw new NotFoundException("{\"message\": \"Пользователь с id=" + id + " не найден\"}");
        }

    }

    @Override
    public Optional<User> getUserById(long id) {
        User user = users.get(id);
        if (user == null) {
            log.info("Пользователь с id={} не найден", id);
        } else {
            log.info("Пользователь с id={} найден", id);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
