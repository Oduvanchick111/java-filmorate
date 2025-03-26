package ru.yandex.practicum.filmorate.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTests {
    UserStorage userStorage;
    UserService userService;

    @BeforeEach
    public void setUp(){
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    public void createUserTest() {
        User user = new User(1L, "pavelboltinskiy@gmail.com", "Oduvanchick", "Pavel", LocalDate.of(1998, 9, 16));
        userService.createUser(user);
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    public void updateUserTest() {
        User user = new User(1L, "pavelboltinskiy@gmail.com", "Oduvanchick", "Pavel", LocalDate.of(1998, 9, 16));
        userService.createUser(user);
        assertEquals("Pavel", userService.findUserById(1L).getName());
        User user1 = new User(1L, "pavelboltinskiy@gmail.com", "Oduvanchick", "NePavel", LocalDate.of(1998, 9, 16));
        userService.updateUser(user1);
        assertEquals("NePavel", userService.findUserById(1L).getName());
    }
}
