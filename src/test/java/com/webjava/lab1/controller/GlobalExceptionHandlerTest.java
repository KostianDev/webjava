package com.webjava.lab1.controller;

import static com.webjava.lab1.web.TraceIdFilter.TRACE_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.webjava.lab1.dto.ProductDTO;
import com.webjava.lab1.service.FeatureNotAvailableException;
import com.webjava.lab1.service.ProductNotFoundException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleNotFoundPropagatesHeaderTraceId() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v3/products/" + UUID.randomUUID());
    request.addHeader(TRACE_ID_HEADER, "trace-123");
    ServletWebRequest webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

    ResponseEntity<ProblemDetail> response =
        handler.handleNotFound(new ProductNotFoundException(UUID.randomUUID()), webRequest);

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getHeaders().getFirst(TRACE_ID_HEADER)).isEqualTo("trace-123");
    ProblemDetail body = Objects.requireNonNull(response.getBody());
    var props = Objects.requireNonNull(body.getProperties());
    assertThat(props.get("path")).isEqualTo(request.getRequestURI());
  }

  @Test
  void handleAllFallsBackToRequestIdHeader() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/internal/error");
    request.addHeader(TRACE_ID_HEADER, "  ");
    request.addHeader("X-Request-ID", "fallback-456");
    ServletWebRequest webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

    ResponseEntity<ProblemDetail> response =
        handler.handleAll(new RuntimeException("boom"), webRequest);

    assertThat(response.getStatusCode().value()).isEqualTo(500);
    assertThat(response.getHeaders().getFirst(TRACE_ID_HEADER)).isEqualTo("fallback-456");
    ProblemDetail body = Objects.requireNonNull(response.getBody());
    var props = Objects.requireNonNull(body.getProperties());
    assertThat(props.get("traceId")).isEqualTo("fallback-456");
  }

  @Test
  void handleValidationReturnsProblemDetailWithViolations() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v3/products");
    ServletWebRequest webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

    ProductDTO target = new ProductDTO();
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "productDTO");
    bindingResult.addError(new FieldError("productDTO", "name", "must not be blank"));
    bindingResult.addError(new FieldError("productDTO", "price", "must be positive"));

    Method method =
        Objects.requireNonNull(DummyController.class.getDeclaredMethod("create", ProductDTO.class));
    MethodParameter parameter = new MethodParameter(method, 0);
    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(parameter, bindingResult);

    ResponseEntity<Object> response = handler.handleValidation(exception, webRequest);

    assertThat(response.getStatusCode().value()).isEqualTo(400);
    assertThat(response.getHeaders().getFirst(TRACE_ID_HEADER)).isNotBlank();
    ProblemDetail body = (ProblemDetail) Objects.requireNonNull(response.getBody());
    var props = Objects.requireNonNull(body.getProperties());
    assertThat(props.get("violations")).isInstanceOf(List.class);
    assertThat((List<?>) props.get("violations")).hasSize(2);
  }

  @Test
  void handleFeatureNotAvailableReturns501WithFeatureName() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/v3/cosmo-cats");
    request.addHeader(TRACE_ID_HEADER, "trace-feature-789");
    ServletWebRequest webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

    ResponseEntity<ProblemDetail> response =
        handler.handleFeatureNotAvailable(
            new FeatureNotAvailableException("cosmoCats"), webRequest);

    assertThat(response.getStatusCode().value()).isEqualTo(501);
    assertThat(response.getHeaders().getFirst(TRACE_ID_HEADER)).isEqualTo("trace-feature-789");
    ProblemDetail body = Objects.requireNonNull(response.getBody());
    var props = Objects.requireNonNull(body.getProperties());
    assertThat(props.get("featureName")).isEqualTo("cosmoCats");
    assertThat(body.getDetail()).contains("cosmoCats");
  }

  private static final class DummyController {

    @SuppressWarnings("unused")
    void create(ProductDTO dto) {}
  }
}
