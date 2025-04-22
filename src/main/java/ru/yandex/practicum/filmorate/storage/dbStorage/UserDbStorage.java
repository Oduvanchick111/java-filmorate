package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.userModel.Status;
import ru.yandex.practicum.filmorate.model.userModel.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.UserRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository("userDbStorage")
public class UserDbStorage extends BaseQuery<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String FRIEND_QUERY = "SELECT friend_id FROM friends WHERE user_id = ? AND status = 'CONFIRMED'";


    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, @Qualifier("userRowMapper") UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getAllUsers() {
        List<User> users = findMany(FIND_ALL_QUERY);
        for (User user : users) {
            user.getFriends().addAll(getFriendsByUserId(user.getId()));
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        long id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        Optional<User> user = findOne(FIND_BY_ID_QUERY, id);
        user.ifPresent(value -> value.setFriends(getFriendsByUserId(id)));
        return user;
    }

    public Set<Long> getFriendsByUserId(Long userId) {
        return new HashSet<>(jdbc.queryForList(FRIEND_QUERY, Long.class, userId));
    }

//    @Override
//    public void addFriend(Long userId, Long friendId) {
//        String pending = Status.PENDING.name();
//        String confirmed = Status.CONFIRMED.name();
//        String checkReverseSql = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
//        List<Map<String, Object>> reverseRequest = jdbc.queryForList(checkReverseSql, friendId, userId);
//        if (!reverseRequest.isEmpty()) {
//            String updateSql1 = "UPDATE friends SET status = 'CONFIRMED' WHERE user_id = ? AND friend_id = ?";
//            update(updateSql1, friendId, userId);
//
//            String updateSql2 = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
//            update(updateSql2, userId, friendId, confirmed);
//        } else {
//            String insertSql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
//            update(insertSql, userId, friendId, pending);
//        }
//    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String confirmed = Status.CONFIRMED.name();
        String updateSql2 = "MERGE INTO friends (user_id, friend_id, status) KEY (user_id, friend_id) VALUES (?, ?, ?)";
        update(updateSql2, userId, friendId, confirmed);
    }

//    @Override
//    public void deleteFriend(Long userId, Long friendId) {
//        String sql = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
//        update(sql, userId, friendId, friendId, userId);
//    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?)";
        jdbc.update(sql, userId, friendId);
    }

    @Override
    public Collection<User> showFriends(Long userId) {
        List<Long> friendsId = jdbc.queryForList(FRIEND_QUERY, Long.class, userId);
        return friendsId.stream()
                .map(id -> findUserById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> showCommonFriends(Long userId, Long friendId) {
        String sql = "SELECT f1.friend_id FROM friends f1 JOIN friends f2 ON f1.friend_id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = 'CONFIRMED' AND f2.status = 'CONFIRMED'";
        List<Long> commonFriends = jdbc.queryForList(sql, Long.class, userId, friendId);
        return commonFriends.stream()
                .map(id -> findUserById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toList());
    }
}
