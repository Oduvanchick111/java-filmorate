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
import ru.yandex.practicum.filmorate.model.filmModel.Film;
import ru.yandex.practicum.filmorate.model.filmModel.Rating;
import ru.yandex.practicum.filmorate.model.userModel.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dbStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, FilmService.class, UserService.class, RatingDbStorage.class, GenreDbStorage.class, UserDbStorage.class, UserRowMapper.class, RatingRowMapper.class, GenreRowMapper.class})
class FilmServiceTests {
    private Long filmId;
    private final FilmService filmService;
    private final UserService userService;

    @BeforeEach
    void setUp() {
        Film film = filmService.createFilm(new Film("Titanic", "Нытье", LocalDate.of(2000, 6, 21), 120, new Rating(1, "G")));
        System.out.println(film);
        filmId = film.getId();
    }

    @Test
    public void findFilmTest() {
        Assertions.assertEquals(filmService.getFilmById(filmId).getName(), "Titanic");
    }

    @Test
    public void updateFilmTest() {
        filmService.updateFilm(new Film(filmId, "Общество Мертвых Поэтов", "Легенда", LocalDate.of(2000, 6, 21), 120, new Rating(1, "G")));
        Assertions.assertEquals(filmService.getFilmById(filmId).getName(), "Общество Мертвых Поэтов");
    }

    @Test
    public void createFilmWithWrongDate() {
        Assertions.assertThrows(ValidateException.class, () -> filmService.createFilm(new Film("Карты, деньги, один ствол", "че?", LocalDate.of(1895, 12, 27), 120, new Rating(1, "G"))));
    }

    @Test
    public void getAllFilmsTest(){
        Collection<Film> films = filmService.getAllFilms();
        Assertions.assertEquals(films.size(), 1);
    }

    @Test
    public void deleteFilmTest() {
        filmService.deleteFilm(filmId);
        Collection<Film> films = filmService.getAllFilms();
        Assertions.assertEquals(films.size(), 0);
    }

    @Test
    public void getFilmByIdYTest() {
        Film film = filmService.getFilmById(filmId);
        Assertions.assertEquals(film.getName(), "Titanic");
    }

    @Test
    public void addLikeTest() {
        User user = userService.createUser(new User("sobaka@mail.ru", "pyatochock", "Pasha", LocalDate.of(1998, 8, 9)));
        filmService.addLike(filmId, user.getId());
        Assertions.assertTrue(filmService.getFilmById(filmId).getIdOfUsersWhoLiked().contains(user.getId()));
    }

    @Test
    public void deleteLikeTest() {
        User user = userService.createUser(new User("sobaka@mail.ru", "pyatochock", "Pasha", LocalDate.of(1998, 8, 9)));
        filmService.addLike(filmId, user.getId());
        Assertions.assertTrue(filmService.getFilmById(filmId).getIdOfUsersWhoLiked().contains(user.getId()));
        filmService.deleteLike(filmId, user.getId());
        Assertions.assertFalse(filmService.getFilmById(filmId).getIdOfUsersWhoLiked().contains(user.getId()));
    }

    @Test
    public void showMostLikedFilmsTest() {
        Film film2 = filmService.createFilm(new Film("Фильм2", "Описание2", LocalDate.of(2000, 6, 21), 120, new Rating(1, "G")));
        Film film3 = filmService.createFilm(new Film("Фильм3", "Описание3", LocalDate.of(2000, 6, 21), 120, new Rating(1, "G")));
        Film film4 = filmService.createFilm(new Film("Фильм4", "Описание4", LocalDate.of(2000, 6, 21), 120, new Rating(1, "G")));
        User user1 = userService.createUser(new User("Чел1", "Пчел1", "Pasha", LocalDate.of(1998, 8, 9)));
        User user2 = userService.createUser(new User("Чел2", "Пчел2", "Pasha", LocalDate.of(1998, 8, 9)));
        User user3 = userService.createUser(new User("Чел3", "Пчел3", "Pasha", LocalDate.of(1998, 8, 9)));
        User user4 = userService.createUser(new User("Чел4", "Пчел4", "Pasha", LocalDate.of(1998, 8, 9)));
        filmService.addLike(filmId, user1.getId());
        filmService.addLike(filmId, user2.getId());
        filmService.addLike(filmId, user3.getId());
        filmService.addLike(filmId, user4.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film4.getId(), user2.getId());
        filmService.addLike(film4.getId(), user3.getId());
        filmService.addLike(film3.getId(), user3.getId());
        Collection<Film> mostLikedFilms = filmService.showMostLikedFilms(2);
        assertThat(mostLikedFilms)
                .isNotNull()
                .isNotEmpty()
                .hasSizeLessThanOrEqualTo(2);
    }
}
