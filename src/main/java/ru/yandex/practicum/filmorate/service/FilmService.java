package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
        filmStorage.createFilm(film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidateException("Id должен быть указан");
        }
        if (filmStorage.getAllFilms().stream().mapToLong(Film::getId).noneMatch(id -> id == film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new NotFoundException("Такой фильм не найден");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        filmStorage.updateFilm(film);
        log.info("был обновлен фильм {}", film);
        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        if (filmStorage.findFilmById(id).isPresent()) {
            return filmStorage.findFilmById(id).get();
        } else {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    public void addLike(Long filmId, Long userId) {
        User user = userService.findUserById(userId);
        Film film = getFilmById(filmId);
        if (film.getIdOfUsersWhoLiked().contains(userId)) {
            throw new ValidateException("Пользователь уже ставил лайк этому фильму");
        }
        film.getIdOfUsersWhoLiked().add(userId);
        log.info("Пользователь {} лайкнул фильм {}", user.getName(), film.getName());
    }

    public void deleteLike(Long filmId, Long userId) {
        User user = userService.findUserById(userId);
        Film film = getFilmById(filmId);
        if (!film.getIdOfUsersWhoLiked().contains(userId)) {
            throw new ValidateException("Пользователь не ставил лайк этому фильму");
        }
        film.getIdOfUsersWhoLiked().remove(userId);
        log.info("Пользователь {} убрал лайк с фильма {}", user.getName(), film.getName());
    }

    public Collection<Film> showMostLikedFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getIdOfUsersWhoLiked().size()).reversed())
                .limit(count)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = filmStorage.getAllFilms()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
