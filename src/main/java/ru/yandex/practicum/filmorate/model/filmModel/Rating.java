package ru.yandex.practicum.filmorate.model.filmModel;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Rating {
    private int id;
    private String name;
}
