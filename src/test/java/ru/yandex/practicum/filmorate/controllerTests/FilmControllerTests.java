package ru.yandex.practicum.filmorate.controllerTests;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;


class FilmControllerTests {

    private MockMvc mockMvc;
    private FilmController filmController;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
    }

    @Test
    void addFilmTest() throws Exception {
        Film film = new Film(1L, "Test Film", "Description", LocalDate.of(2000, 1, 1), 120);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        FilmController filmController = new FilmController();
        Film film = new Film(1L, "Old Film", "Description", LocalDate.of(1800, 1, 1), 120);

        ValidateException exception = assertThrows(ValidateException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void updateFilmTest() throws Exception {
        Film film = new Film(1L, "Film", "Description", LocalDate.of(2008, 10, 5), 120);
        filmController.addFilm(film);

        Film updatedFilm = new Film(1L, "Updated Film", "New Description", LocalDate.of(2010, 5, 15), 150);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film"));
    }

    @Test
    void getFilmsTest() throws Exception {
        Film film1 = new Film(1L, "Film One", "Description One", LocalDate.of(2000, 1, 1), 120);
        Film film2 = new Film(2L, "Film Two", "Description Two", LocalDate.of(2010, 5, 5), 130);
        filmController.addFilm(film1);
        filmController.addFilm(film2);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
