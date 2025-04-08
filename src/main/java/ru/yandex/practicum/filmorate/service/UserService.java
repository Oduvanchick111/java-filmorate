package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        user.setName(checkName(user.getName(), user.getLogin()));
        user.setId(getNextId());
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
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        if (user.getFriends().contains(friendId)) {
            throw new ValidateException("Пользователи уже друзья");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} теперь друзья", user.getName(), friend.getName());
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidateException("Невозможно удалить из друзей самого себя");
        }
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи {} и {} теперь не друзья :(((((((((((((((((", user.getName(), friend.getName());
    }

    public Collection<User> showFriends(Long userId) {
        User user = findUserById(userId);
        Set<User> friends = new HashSet<>();
        for (Long friendId : user.getFriends()) {
            User friend = findUserById(friendId);
            friends.add(friend);
        }
        return friends;
    }

    public Collection<User> showCommonFriends(Long userId, Long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        Set<User> commonFriends = new HashSet<>();
        for (Long usersId : user.getFriends()) {
            if (friend.getFriends().contains(usersId)) {
                commonFriends.add(findUserById(usersId));
            }
        }
        return commonFriends;
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
