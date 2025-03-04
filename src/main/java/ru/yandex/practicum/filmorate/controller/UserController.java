package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private String checkName(String name, String login) {
        if (name == null || name.isBlank()) {
            name = login;
        }
        return name;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        user.setName(checkName(user.getName(), user.getLogin()));
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: '{}'", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.error("Ошибка валидации: id не может быть null");
            throw new ValidateException("Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            log.error("Ошибка валидации: такого id '{}' не существует", user.getId());
            throw new ValidateException("Пользователь с таким id не найден");
        }
        user.setName(checkName(user.getName(), user.getLogin()));
        users.put(user.getId(), user);
        log.info("Пользователь {} обновлен", user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
