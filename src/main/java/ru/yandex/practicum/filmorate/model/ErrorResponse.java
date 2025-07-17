package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private long timestamp;
    private Map<String, String> fieldErrors;

    public ErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorResponse(int status, String message, long timestamp,
                         Map<String, String> fieldErrors) {
        this(status, message, timestamp);
        this.fieldErrors = fieldErrors;
    }

}
