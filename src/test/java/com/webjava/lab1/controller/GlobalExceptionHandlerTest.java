package com.webjava.lab1.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.service.ProductNotFoundException;
import com.webjava.lab1.web.TraceIdFilter;
import java.util.UUID;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleNotFoundPropagatesHeaderTraceId() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1.1/products/" + UUID.randomUUID());
    request.addHeader(TraceIdFilter.TRACE_ID_HEADER, "trace-123");
    ServletWebRequest webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

    ResponseEntity<ProblemDetail> response =
        handler.handleNotFound(new ProductNotFoundException(UUID.randomUUID()), webRequest);

    assertThat(response.getStatusCode().value()).isEqualTo(404);
  assertThat(response.getHeaders().getFirst(TraceIdFilter.TRACE_ID_HEADER)).isEqualTo("trace-123");
  assertThat(response.getBody()).isNotNull();
  ProblemDetail body = Objects.requireNonNull(response.getBody());
  var props = Objects.requireNonNull(body.getProperties());
  assertThat(props.get("path")).isEqualTo(request.getRequestURI());
  }

  @Test
  void handleAllFallsBackToRequestIdHeader() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/internal/error");
    request.addHeader(TraceIdFilter.TRACE_ID_HEADER, "  ");
    request.addHeader("X-Request-ID", "fallback-456");
    ServletWebRequest webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

    ResponseEntity<ProblemDetail> response =
        handler.handleAll(new RuntimeException("boom"), webRequest);

    assertThat(response.getStatusCode().value()).isEqualTo(500);
  assertThat(response.getHeaders().getFirst(TraceIdFilter.TRACE_ID_HEADER)).isEqualTo("fallback-456");
    assertThat(response.getBody()).isNotNull();
  ProblemDetail body = Objects.requireNonNull(response.getBody());
  var props = Objects.requireNonNull(body.getProperties());
  assertThat(props.get("traceId")).isEqualTo("fallback-456");
  }
}
