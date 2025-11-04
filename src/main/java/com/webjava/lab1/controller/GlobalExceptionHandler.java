package com.webjava.lab1.controller;

import com.webjava.lab1.service.ProductNotFoundException;
import com.webjava.lab1.web.TraceIdFilter;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @NonNull
  private String resolveTraceId(WebRequest request) {
    Object attribute =
        request.getAttribute(TraceIdFilter.TRACE_ID_ATTRIBUTE, WebRequest.SCOPE_REQUEST);
    if (attribute instanceof String s && !s.isBlank()) {
      return s;
    }

    String trace = request.getHeader(TraceIdFilter.TRACE_ID_HEADER);
    if (trace != null && !trace.isBlank()) {
      return trace;
    }

    String fallback = request.getHeader("X-Request-ID");
    if (fallback != null && !fallback.isBlank()) {
      return fallback;
    }

    return java.util.UUID.randomUUID().toString();
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
    String path = resolvePath(request);
    String traceId = resolveTraceId(request);

    List<Map<String, Object>> violations = new ArrayList<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> {
              Map<String, Object> violation = new LinkedHashMap<>();
              violation.put("field", error.getField());
              violation.put("message", error.getDefaultMessage());
              violations.add(violation);
            });
    ex.getBindingResult()
        .getGlobalErrors()
        .forEach(
            error -> {
              Map<String, Object> violation = new LinkedHashMap<>();
              violation.put("object", error.getObjectName());
              violation.put("message", error.getDefaultMessage());
              violations.add(violation);
            });

    ProblemDetail body = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    body.setType(URI.create("https://example.com/probs/validation"));
    body.setTitle("Validation Failed");
    body.setDetail(messageDetail);
    body.setInstance(toInstanceUri(path));
    body.setProperty("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
    body.setProperty("message", messageDetail);
    body.setProperty("path", path);
    body.setProperty("timestamp", Instant.now());
    body.setProperty("traceId", traceId);
    body.setProperty("violations", violations);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header(TraceIdFilter.TRACE_ID_HEADER, traceId)
        .body(body);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(
      ProductNotFoundException ex, WebRequest request) {
    String traceId = resolveTraceId(request);
    String path = resolvePath(request);

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    pd.setType(URI.create("https://example.com/probs/not-found"));
    pd.setTitle("Resource Not Found");
    pd.setDetail(ex.getMessage());
    pd.setInstance(toInstanceUri(path));
    pd.setProperty("timestamp", Instant.now());
    pd.setProperty("traceId", traceId);
    pd.setProperty("path", path);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header(TraceIdFilter.TRACE_ID_HEADER, traceId)
        .body(pd);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleAll(Exception ex, WebRequest request) {
    String traceId = resolveTraceId(request);
    String path = resolvePath(request);

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setType(URI.create("https://example.com/probs/internal"));
    pd.setTitle("Internal Server Error");
    pd.setDetail(ex.getMessage());
    pd.setInstance(toInstanceUri(path));
    pd.setProperty("timestamp", Instant.now());
    pd.setProperty("traceId", traceId);
    pd.setProperty("path", path);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .header(TraceIdFilter.TRACE_ID_HEADER, traceId)
        .body(pd);
  }

  @NonNull
  private String resolvePath(WebRequest request) {
    if (request instanceof ServletWebRequest servletRequest) {
      String uri = servletRequest.getRequest().getRequestURI();
      if (uri != null && !uri.isBlank()) {
        return uri;
      }
    }

    String description = request.getDescription(false);
    if (description != null && description.startsWith("uri=")) {
      description = description.substring(4);
    }

    if (description == null || description.isBlank()) {
      return "/";
    }

    return description;
  }

  @NonNull
  private URI toInstanceUri(String path) {
    String candidate = path != null && !path.isBlank() ? path : "/";
    if (!candidate.startsWith("/")) {
      candidate = "/" + candidate;
    }
    try {
      return URI.create(candidate);
    } catch (IllegalArgumentException ex) {
      return URI.create("/");
    }
  }
}
