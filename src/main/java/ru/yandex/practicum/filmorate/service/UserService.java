package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.userModel.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        user.setName(checkName(user.getName(), user.getLogin()));
        userStorage.createUser(user);
        log.info("Пользователь создан: '{}'", user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.error("Ошибка валидации: id не может быть null");
            throw new ValidateException("Id должен быть указан");
        }
        if (userStorage.getAllUsers().stream().mapToLong(User::getId).noneMatch(id -> id == user.getId())) {
            log.error("Ошибка валидации: такого id '{}' не существует", user.getId());
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        user.setName(checkName(user.getName(), user.getLogin()));
        userStorage.updateUser(user);
        log.info("Пользователь {} обновлен", user);
        return user;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User findUserById(Long id) {
        if (userStorage.findUserById(id).isPresent()) {
            return userStorage.findUserById(id).get();
        } else {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidateException("Невозможно добавить в друзья самого себя");
        }
        User user = checkValidationUser(userId);
        User friend = checkValidationUser(friendId);
        userStorage.addFriend(userId, friendId);
        if (userStorage.showFriends(userId).contains(friend)) {
            log.info("Пользователи {} и {} теперь друзья", user.getLogin(), friend.getLogin());
        } else {
            log.info("Пользователь {} отправил запрос дружбы пользователю {}", user.getLogin(), friend.getLogin());
        }

    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidateException("Невозможно удалить из друзей самого себя");
        }
        User user = checkValidationUser(userId);
        User friend = checkValidationUser(friendId);
        userStorage.deleteFriend(userId, friendId);
        log.info("Пользователи {} и {} теперь не друзья :(((((((((((((((((", user.getName(), friend.getName());
    }

    public Collection<User> showFriends(Long userId) {
        checkValidationUser(userId);
        return userStorage.showFriends(userId);
    }

    public Collection<User> showCommonFriends(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidateException("Невозможно добавить в друзья самого себя");
        }
        checkValidationUser(userId);
        checkValidationUser(friendId);
        return userStorage.showCommonFriends(userId, friendId);
    }

    protected User checkValidationUser(Long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private long getNextId() {
        long currentMaxId = userStorage.getAllUsers()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private String checkName(String name, String login) {
        if (name == null || name.isBlank()) {
            name = login;
        }
        return name;
    }
}
