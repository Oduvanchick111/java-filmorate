package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Film.
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Film {
    private Long id;
    @NotNull (message = "Имя не может быть пустым")
    @NotBlank (message = "Имя не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    @Past(message = "Дата релиза должна быть до сегодняшнего дня")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть отрицательной либо 0")
    private int duration;

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
