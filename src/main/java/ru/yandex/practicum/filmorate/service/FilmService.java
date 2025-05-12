package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.filmModel.Film;
import ru.yandex.practicum.filmorate.model.filmModel.Genre;
import ru.yandex.practicum.filmorate.model.filmModel.Rating;
import ru.yandex.practicum.filmorate.model.userModel.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.RatingStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService, GenreStorage genreStorage, RatingStorage ratingStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
    }

    public Film createFilm(Film film) {
        checkValidateGenre(film);
        checkValidateMpa(film);
        checkReleaseDate(film);
        Film savedFilm = filmStorage.createFilm(film);
        log.info("Добавлен фильм {}", film);
        return getFilmById(savedFilm.getId());
    }

    public Film updateFilm(Film film) {
        checkValidateFilm(film.getId());
        if (film.getId() == null) {
            throw new ValidateException("Id должен быть указан");
        }
        checkValidateGenre(film);
        checkValidateMpa(film);
        checkReleaseDate(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("был обновлен фильм {}", film);
        return getFilmById(updatedFilm.getId());
    }

    public Collection<Film> getAllFilms() {
        log.info("Был получен запрос на просмотр всех имеющихся фильмов");
        return filmStorage.getAllFilms();
    }

    public void deleteFilm(Long filmId) {
        checkValidateFilm(filmId);
        filmStorage.deleteFilm(filmId);
        log.info("Фильм с Id {} был удален", filmId);
    }

    public Film getFilmById(Long id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с таким id не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        User user = checkValidationUser(userId);
        Film film = getFilmById(filmId);
        if (film.getIdOfUsersWhoLiked().contains(userId)) {
            throw new ValidateException("Пользователь уже ставил лайк этому фильму");
        }
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} лайкнул фильм {}", user.getName(), film.getName());
    }

    public void deleteLike(Long filmId, Long userId) {
        User user = checkValidationUser(userId);
        Film film = getFilmById(filmId);
        if (!film.getIdOfUsersWhoLiked().contains(userId)) {
            throw new ValidateException("Пользователь не ставил лайк этому фильму");
        }
        filmStorage.deleteLike(filmId, userId);
        log.info("Пользователь {} убрал лайк с фильма {}", user.getName(), film.getName());
    }

    public Collection<Film> showMostLikedFilms(int count) {
        log.info("Был запрос на получение {} самых популярных фильмов", count);
        return filmStorage.showMostLikedFilms(count);
    }

    private long getNextId() {
        long currentMaxId = filmStorage.getAllFilms()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkValidateFilm(Long filmId) {
        if (filmStorage.findFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private User checkValidationUser(Long userId) {
        return userService.checkValidationUser(userId);
    }

    private void checkValidateGenre(Film film) {
        Set<Integer> genresId;
        genresId = genreStorage.getAllGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        for (Genre genre : film.getGenres()) {
            if (!genresId.contains(genre.getId())) {
                throw new NotFoundException("Такого жанра нет");
            }
        }
    }

    private void checkValidateMpa(Film film) {
        Set<Integer> ratingsId = new HashSet<>();
        ratingsId = ratingStorage.getAllRatings().stream().map(Rating::getId).collect(Collectors.toSet());
        if (!ratingsId.contains(film.getRating().getId())) {
            throw new NotFoundException("Такого рейтинга нет");
        }
    }
}
