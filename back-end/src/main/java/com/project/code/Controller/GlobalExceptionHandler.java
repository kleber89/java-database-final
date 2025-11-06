package com.project.code.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de parseo/JSON inválido en el body de la petición.
     * Devuelve 400 Bad Request y un cuerpo con un mensaje legible.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleJsonParseException(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invalid input: The data provided is not valid.");
        return body;
    }

}
