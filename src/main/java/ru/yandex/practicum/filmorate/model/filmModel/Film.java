package ru.yandex.practicum.filmorate.model.filmModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@ToString
public class Film {
    private Long id;
    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    @PastOrPresent(message = "Дата релиза фильма не может быть в будущем")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть отрицательной либо 0")
    private int duration;
    @JsonProperty("mpa")
    private Rating rating;
    private Set<Genre> genres = new HashSet<>();

    private Set<Long> idOfUsersWhoLiked = new HashSet<>();

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, Rating rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, Rating rating) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
    }

    public Film() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
