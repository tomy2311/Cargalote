package com.cargalote.cargalote.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorValidacion(
            MethodArgumentNotValidException e) {

        Map<String, String> errores = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("status", 400);
        respuesta.put("error", "Datos inválidos");
        respuesta.put("errores", errores);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(
            HttpMessageNotReadableException e) {

        logger.warn(
                "JSON inválido recibido: {}",
                e.getMostSpecificCause().getMessage()
        );

        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("status", 400);
        respuesta.put("error", "JSON inválido");
        respuesta.put("mensaje", "El formato de los datos enviados no es válido.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception e) {

        logger.error("Error interno del servidor:", e);

        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("status", 500);
        respuesta.put("error", "Error interno del servidor");
        respuesta.put("mensaje", "Ocurrió un error al procesar la solicitud.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(respuesta);
    }
}