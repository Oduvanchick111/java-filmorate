package ru.yandex.practicum.filmorate.storage.dbStorage.interfaces;

import ru.yandex.practicum.filmorate.model.userModel.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    Optional<User> findUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> showFriends(Long userId);

    Collection<User> showCommonFriends(Long userId, Long friendId);

}
