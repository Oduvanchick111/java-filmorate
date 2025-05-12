package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.filmModel.Film;
import ru.yandex.practicum.filmorate.model.filmModel.Genre;
import ru.yandex.practicum.filmorate.storage.dbStorage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dbStorage.mappers.GenreRowMapper;

import java.sql.Date;

import java.util.*;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage extends BaseQuery<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS rating
                FROM films f
                LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
            """;
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS rating FROM films f " +
            "LEFT JOIN MPA m ON f.mpa_id = m.mpa_id " +
            "WHERE f.film_id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, @Qualifier("filmRowMapper") FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = findMany(FIND_ALL_QUERY);

        Map<Long, Set<Genre>> genresByFilmId = getGenresForAllFilms();
        Map<Long, Set<Long>> likesByFilmId = getLikesForAllFilms();

        for (Film film : films) {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), Set.of()));
            film.setIdOfUsersWhoLiked(likesByFilmId.getOrDefault(film.getId(), Set.of()));
        }

        return films;
    }

    @Override
    public Film createFilm(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getRating().getId());

        film.setId(id);
        insertGenresForFilm(id, film.getGenres());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRating().getId(), film.getId());
        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES(?, ?)", film.getId(), genre.getId());
        }
        return findFilmById(film.getId()).orElseThrow(() -> new NotFoundException("Ошибка создания фильма"));
    }

    @Override
    public void deleteFilm(Long filmId) {
        delete(DELETE_QUERY, filmId);
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, filmId);
        film.ifPresent(value -> {
            value.setGenres(getGenresForFilm(filmId));
            value.setIdOfUsersWhoLiked(getIdOfUsersWhoLiked(filmId));
        });
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO user_likes (film_id, user_id) VALUES (?, ?)";
        update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM user_likes WHERE film_id = ? AND user_id = ?";
        update(sql, filmId, userId);
    }

    @Override
    public Collection<Film> showMostLikedFilms(int count) {
        String sql = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS rating
                FROM films f
                LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
                LEFT JOIN user_likes ul ON f.film_id = ul.film_id
                GROUP BY f.film_id, m.name
                ORDER BY COUNT(ul.user_id) DESC
                LIMIT ?
                """;
        List<Film> films = findMany(sql, count);
        for (Film film : films) {
            film.setGenres(getGenresForFilm(film.getId()));
            film.setIdOfUsersWhoLiked(getIdOfUsersWhoLiked(film.getId()));
        }
        return films;
    }

    private Set<Genre> getGenresForFilm(Long filmId) {
        String sql = """
                    SELECT g.genre_id, g.genre
                    FROM film_genre fg
                    JOIN genre g ON fg.genre_id = g.genre_id
                    WHERE fg.film_id = ?
                    ORDER BY g.genre_id
                """;
        return new LinkedHashSet<>(jdbc.query(sql, new GenreRowMapper(), filmId));
    }

    private Map<Long, Set<Genre>> getGenresForAllFilms() {
        String sql = """
                    SELECT fg.film_id, g.genre_id, g.genre
                    FROM film_genre fg
                    JOIN genre g ON fg.genre_id = g.genre_id
                    ORDER BY g.genre_id
                """;

        return jdbc.query(sql, rs -> {
            Map<Long, Set<Genre>> filmGenres = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre"));
                filmGenres.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
            }
            return filmGenres;
        });
    }

    private Set<Long> getIdOfUsersWhoLiked(Long filmId) {
        String sql = "SELECT user_id FROM user_likes WHERE film_id = ?";
        List<Long> userIds = jdbc.queryForList(sql, Long.class, filmId);
        return new HashSet<>(userIds);
    }

    private Map<Long, Set<Long>> getLikesForAllFilms() {
        String sql = """
                    SELECT film_id, user_id
                    FROM user_likes
                """;

        return jdbc.query(sql, rs -> {
            Map<Long, Set<Long>> likesByFilmId = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long userId = rs.getLong("user_id");
                likesByFilmId
                        .computeIfAbsent(filmId, k -> new HashSet<>())
                        .add(userId);
            }
            return likesByFilmId;
        });

    }

    private void insertGenresForFilm(Long filmId, Set<Genre> genres) {
        if (genres.isEmpty()) return;

        StringBuilder sql = new StringBuilder("INSERT INTO film_genre (film_id, genre_id) VALUES ");
        List<Object> params = new ArrayList<>();

        int count = 0;
        for (Genre genre : genres) {
            if (count > 0) sql.append(", ");
            sql.append("(?, ?)");
            params.add(filmId);
            params.add(genre.getId());
            count++;
        }

        jdbc.update(sql.toString(), params.toArray());
    }
}
