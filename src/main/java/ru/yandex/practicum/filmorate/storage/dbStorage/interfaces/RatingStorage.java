package ru.yandex.practicum.filmorate.storage.dbStorage.interfaces;

import ru.yandex.practicum.filmorate.model.filmModel.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingStorage {
    public List<Rating> getAllRatings();

    public Optional<Rating> getRatingById(int id);
}
