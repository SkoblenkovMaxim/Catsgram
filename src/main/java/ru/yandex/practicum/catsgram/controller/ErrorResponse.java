package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorResponse {
    private final String error;

    @Autowired
    public ErrorResponse(String error) {
        this.error = error;
    }
}
