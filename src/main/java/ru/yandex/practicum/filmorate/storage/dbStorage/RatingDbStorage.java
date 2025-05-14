package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.filmModel.Rating;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.RatingStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.RatingRowMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository("ratingDbStorage")
public class RatingDbStorage extends BaseQuery<Rating> implements RatingStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM MPA ORDER BY mpa_id";
    private static final String FIND_ONE_QUERY = "SELECT * FROM MPA WHERE mpa_id = ?";

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbc, @Qualifier("ratingRowMapper") RatingRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Rating> getAllRatings() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Rating> getRatingById(int id) {
        return findOne(FIND_ONE_QUERY, id);
    }
}
