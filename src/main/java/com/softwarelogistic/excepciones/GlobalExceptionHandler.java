package com.softwarelogistic.excepciones;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * ====================================================================
 * MANEJADOR GLOBAL DE EXCEPCIONES Y ERRORES
 * ====================================================================
 * Intercepta cualquier fallo en la aplicación para devolver respuestas
 * HTTP profesionales y claras en formato JSON en vez de páginas de error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. Captura fallos de validación (@NotBlank, @NotNull, @Email, @Size)
     * Devuelve HTTP 400 Bad Request con un mapa campo -> mensaje.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErroresValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errores.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    /**
     * 2. Captura errores de integridad referencial de la Base de Datos
     * (Ejemplo: Llaves foráneas, datos duplicados únicos como el correo).
     * Devuelve HTTP 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> manejarViolacionIntegridad(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Operación no permitida: El registro está vinculado a otros datos o ya existe un valor duplicado.");
    }

    /**
     * 3. Captura cualquier otro error no previsto en el servidor.
     * Devuelve HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcionGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error en el servidor: " + ex.getMessage());
    }
}