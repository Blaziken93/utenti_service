package it.exprivia.utenti.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Intercetta le eccezioni lanciate dai controller/service e le trasforma
 * in risposte JSON chiare invece del messaggio di errore grezzo di Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Risorsa non trovata → 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Dati non validi (email duplicata, utente già nel gruppo, ecc.) → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Errori di validazione (@Valid sui DTO) → 400 con dettaglio campo per campo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String messaggio = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Dati non validi");
        return buildResponse(HttpStatus.BAD_REQUEST, messaggio);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String messaggio) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("errore", messaggio);
        return ResponseEntity.status(status).body(body);
    }
}
