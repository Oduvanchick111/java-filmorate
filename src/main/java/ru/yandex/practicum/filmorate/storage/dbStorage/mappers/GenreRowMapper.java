package ru.yandex.practicum.filmorate.storage.dbStorage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.filmModel.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("genreRowMapper")
public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre"));
    }
}
