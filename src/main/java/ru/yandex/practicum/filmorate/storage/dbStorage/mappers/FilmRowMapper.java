package ru.yandex.practicum.filmorate.storage.dbStorage.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.filmModel.Film;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.filmModel.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component("filmRowMapper")
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        film.setReleaseDate(releaseDate);
        film.setDuration(rs.getInt("duration"));
        int ratingId = rs.getInt("mpa_id");
        String ratingName = rs.getString("rating");
        film.setRating(new Rating(ratingId, ratingName));
        return film;
    }
}
