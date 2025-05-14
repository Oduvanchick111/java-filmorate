package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.filmModel.Genre;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.GenreRowMapper;

import java.util.List;
import java.util.Optional;


@Slf4j
@Repository("genreDbStorage")
public class GenreDbStorage extends BaseQuery<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre ORDER BY genre_id";
    private static final String FIND_ONE_QUERY = "SELECT * FROM genre WHERE genre_id = ?";

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc, @Qualifier("genreRowMapper") GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> getAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return findOne(FIND_ONE_QUERY, id);
    }
}
