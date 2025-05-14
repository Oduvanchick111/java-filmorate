package ru.yandex.practicum.filmorate.storage.dbStorage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.filmModel.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("ratingRowMapper")
public class RatingRowMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Rating(rs.getInt("mpa_id"), rs.getString("name"));
    }
}
