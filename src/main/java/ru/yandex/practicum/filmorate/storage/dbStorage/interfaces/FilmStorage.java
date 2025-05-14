package ru.yandex.practicum.filmorate.storage.dbStorage.interfaces;

import ru.yandex.practicum.filmorate.model.filmModel.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Long filmId);

    Optional<Film> findFilmById(Long id);

     void addLike(Long filmId, Long userId);


     void deleteLike(Long filmId, Long userId);

     Collection<Film> showMostLikedFilms(int count);
}
