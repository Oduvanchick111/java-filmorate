package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.filmModel.Rating;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.RatingStorage;

import java.util.Collection;

@Slf4j
@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Rating getRatingById(int id) {
        return ratingStorage.getRatingById(id)
                .orElseThrow(() -> new NotFoundException("Такой тип рейтинга не найден"));
    }

    public Collection<Rating> getAllRatings() {
        return ratingStorage.getAllRatings();
    }
}
