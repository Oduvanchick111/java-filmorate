package ru.yandex.practicum.filmorate.storage.dbStorage.interfaces;

import ru.yandex.practicum.filmorate.model.filmModel.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    public List<Genre> getAllGenres();

    public Optional<Genre> getGenreById(int id);
}
