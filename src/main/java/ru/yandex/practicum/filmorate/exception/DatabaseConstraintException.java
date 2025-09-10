package ru.yandex.practicum.filmorate.exception;

public class DatabaseConstraintException extends RuntimeException {
    public DatabaseConstraintException(String message) {
        super(message);
    }

    public DatabaseConstraintException(String message, Throwable cause) {
        super(message, cause);
    }
}
