package com.webjava.lab1.controller;

import com.webjava.lab1.service.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
// ...existing code...

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        ProblemDetails pd = new ProblemDetails();
        pd.setType("https://example.com/probs/validation");
        pd.setTitle("Validation Failed");
        pd.setStatus(HttpStatus.BAD_REQUEST.value());
        pd.setDetail("One or more validation errors occurred");
        pd.setInstance(request.getDescription(false).replace("uri=", ""));
        pd.setTimestamp(Instant.now());
        List<Violation> violations = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> violations.add(new Violation(fe.getField(), fe.getDefaultMessage())));
        pd.setViolations(violations);
        return new ResponseEntity<>(pd, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetails> handleNotFound(ProductNotFoundException ex, WebRequest request) {
        ProblemDetails pd = new ProblemDetails();
        pd.setType("https://example.com/probs/not-found");
        pd.setTitle("Resource Not Found");
        pd.setStatus(HttpStatus.NOT_FOUND.value());
        pd.setDetail(ex.getMessage());
        pd.setInstance(request.getDescription(false).replace("uri=", ""));
        pd.setTimestamp(Instant.now());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleAll(Exception ex, WebRequest request) {
        ProblemDetails pd = new ProblemDetails();
        pd.setType("https://example.com/probs/internal");
        pd.setTitle("Internal Server Error");
        pd.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        pd.setDetail(ex.getMessage());
        pd.setInstance(request.getDescription(false).replace("uri=", ""));
        pd.setTimestamp(Instant.now());
        return new ResponseEntity<>(pd, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
