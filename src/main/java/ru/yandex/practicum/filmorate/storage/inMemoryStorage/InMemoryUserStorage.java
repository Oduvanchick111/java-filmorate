package ru.yandex.practicum.filmorate.storage.inMemoryStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.userModel.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.UserStorage;

import java.util.*;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = findUserById(userId).get();
        User friend = findUserById(friendId).get();
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = findUserById(userId).get();
        User friend = findUserById(friendId).get();
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public Collection<User> showFriends(Long userId) {
        User user = findUserById(userId).get();
        List<User> friends = new ArrayList<>();
        friends.add(user);
        return friends;
    }

    @Override
    public Collection<User> showCommonFriends(Long userId, Long friendId) {
        User user = findUserById(userId).get();
        User friend = findUserById(friendId).get();
        Set<User> commonFriends = new HashSet<>();
        for (Long usersId : user.getFriends()) {
            if (friend.getFriends().contains(usersId)) {
                commonFriends.add(findUserById(usersId).get());
            }
        }
        return commonFriends;
    }
}
