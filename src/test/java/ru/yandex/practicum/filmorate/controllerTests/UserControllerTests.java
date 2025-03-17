package ru.yandex.practicum.filmorate.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTests {
    private MockMvc mockMvc;
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        userController = new UserController();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void addUser() throws Exception {
        User user = new User(1L, "pavelboltinskiy@gmail.com", "Oduvanchick", "Pavel", LocalDate.of(1998, 9, 16));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void updateUserTest() throws Exception {
        User user = new User(1L, "pavelboltinskiy@gmail.com", "Oduvanchick", "Pavel", LocalDate.of(1998, 9, 16));
        userController.addUser(user);

        User updatedUser = new User(1L, "pboltinskiy@yandex.ru", "Oduvanchick", "Павел", LocalDate.of(1998, 9, 16));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Павел"));
    }

    @Test
    void getUsersTest() throws Exception {
        User user1 = new User(1L, "pavelboltinskiy@gmail.com", "Oduvanchick", "Pavel", LocalDate.of(1998, 9, 16));
        User user2 = new User(2L, "ktoeto@gmail.com", "neOduvanchick", "nePavel", LocalDate.of(1993, 9, 16));
        userController.addUser(user1);
        userController.addUser(user2);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturnLoginInsteadOfName() throws Exception {
        User user = new User(1L, "pavelboltinskiy@gmail.com", "Oduvanchick", null, LocalDate.of(1998, 9, 16));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Oduvanchick"));
    }
}
