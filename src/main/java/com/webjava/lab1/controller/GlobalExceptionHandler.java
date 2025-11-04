package com.webjava.lab1.controller;

import com.webjava.lab1.service.ProductNotFoundException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  private String resolveTraceId(WebRequest request) {
    String trace = request.getHeader("X-Trace-Id");
    if (trace == null) {
      trace = request.getHeader("X-Request-ID");
    }
    if (trace == null) {
      trace = java.util.UUID.randomUUID().toString();
    }
    return trace;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidation(
      MethodArgumentNotValidException ex, WebRequest request) {
    org.springframework.validation.FieldError fieldError = ex.getBindingResult().getFieldError();
    String objectName = ex.getBindingResult().getObjectName();
    String messageDetail = "Validation failed";
    if (fieldError != null) {
      messageDetail =
          String.format(
              "Validation failed for object '%s': Field '%s' %s.",
              objectName, fieldError.getField(), fieldError.getDefaultMessage());
    }
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
    body.put("message", messageDetail);
    body.put("path", request.getDescription(false).replace("uri=", ""));
    String traceId = resolveTraceId(request);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("X-Trace-Id", traceId).body(body);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ProblemDetails> handleNotFound(
      ProductNotFoundException ex, WebRequest request) {
    ProblemDetails pd = new ProblemDetails();
    pd.setType("https://example.com/probs/not-found");
    pd.setTitle("Resource Not Found");
    pd.setStatus(HttpStatus.NOT_FOUND.value());
    pd.setDetail(ex.getMessage());
    pd.setInstance(request.getDescription(false).replace("uri=", ""));
    pd.setTimestamp(Instant.now());
    String traceId = resolveTraceId(request);
    pd.setTraceId(traceId);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).header("X-Trace-Id", traceId).body(pd);
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
    String traceId = resolveTraceId(request);
    pd.setTraceId(traceId);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .header("X-Trace-Id", traceId)
        .body(pd);
  }
}
