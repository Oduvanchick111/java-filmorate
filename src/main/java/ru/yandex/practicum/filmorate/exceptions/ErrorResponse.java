package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {
    private final String error;
    private final String description;
    private final Map<String, String> fields;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
        this.fields = null;
    }
}
