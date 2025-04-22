package ru.yandex.practicum.filmorate.controllerTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.userModel.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.ArrayList;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, UserService.class})

public class UserServiceTests {

    private Long userId;
    private final UserService userService;

    @BeforeEach
    void setUp() {
        User user = userService.createUser(new User("sobaka@mail.ru", "pyatochock", "Pasha", LocalDate.of(1998, 8, 9)));
        this.userId = user.getId();
    }

    @Test
    public void findUserTest() {
        Assertions.assertEquals(userService.findUserById(userId).getName(), "Pasha");
    }

    @Test
    public void updateUserTest() {
        userService.updateUser(new User(userId, "sobaka@mail.ru", "pyatochock", "Andrey", LocalDate.of(1998, 8, 9)));
        Assertions.assertEquals(userService.findUserById(userId).getName(), "Andrey");
    }

    @Test
    public void createUserWithEmptyNameTest() {
        User user1 = userService.createUser(new User("sobaka@mail.ru", "pyatochock", "", LocalDate.of(1998, 8, 9)));
        Assertions.assertEquals(userService.findUserById(user1.getId()).getName(), "pyatochock");
    }

    @Test
    public void getAllUsersTest() {
        User user1 = userService.createUser(new User("sobaka@mail.ru", "pyatochock", "", LocalDate.of(1998, 8, 9)));
        Assertions.assertEquals(userService.getAllUsers().size(), 2);
    }

    @Test
    public void addFriendTest() {
        User user1 = userService.createUser(new User("sobaka@mail.ru", "pyatochock", "Pavel", LocalDate.of(1998, 8, 9)));
        Assertions.assertEquals(userService.findUserById(userId).getFriends().size(), 0);
        userService.addFriend(userService.findUserById(userId).getId(), user1.getId());
        Assertions.assertEquals(userService.findUserById(userId).getFriends().size(), 1);
    }

    @Test
    public void addYourselfAsAFriend() {
        Assertions.assertThrows(ValidateException.class, () -> userService.addFriend(userId, userId));
    }

    @Test
    public void deleteFriendTest() {

        User user1 = userService.createUser(new User("sobaka@mail.ru", "pyatochock", "Pavel", LocalDate.of(1998, 8, 9)));
        userService.addFriend(userService.findUserById(userId).getId(), user1.getId());
        Assertions.assertEquals(userService.findUserById(userId).getFriends().size(), 1);
        userService.deleteFriend(userId, user1.getId());
        Assertions.assertEquals(userService.findUserById(userId).getFriends().size(), 0);
    }

    @Test
    public void showFriendsTest() {
        addFriendTest();
        ArrayList<User> friends = (ArrayList<User>) userService.showFriends(userId);
        Assertions.assertEquals(friends.get(0).getLogin(), "pyatochock");
    }

}