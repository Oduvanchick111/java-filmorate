package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    public Collection<User> getAllUsers();

    public User createUser(User user);

    public User updateUser(User user);

    public Optional<User> findUserById(Long id);
}
