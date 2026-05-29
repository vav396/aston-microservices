package com.aston.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Пользователь с идентификатором " + id + " не найден");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}