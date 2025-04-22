package ru.yandex.practicum.filmorate.storage.inMemoryStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.filmModel.Film;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.FilmStorage;

import java.util.*;

@Component("userInMemoryStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(Long filmId) {
        films.remove(filmId);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        films.get(filmId).getIdOfUsersWhoLiked().add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        films.get(filmId).getIdOfUsersWhoLiked().remove(userId);
    }

    @Override
    public Collection<Film> showMostLikedFilms(int count) {
        return films.values().stream().sorted(Comparator.comparingInt(f -> -f.getIdOfUsersWhoLiked().size()))
                .limit(count)
                .toList();
    }
}
