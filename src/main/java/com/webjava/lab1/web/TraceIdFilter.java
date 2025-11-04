package com.webjava.lab1.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

  public static final String TRACE_ID_ATTRIBUTE = TraceIdFilter.class.getName() + ".TRACE_ID";
  public static final String TRACE_ID_HEADER = "X-Trace-Id";
  private static final String FALLBACK_HEADER = "X-Request-ID";

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = resolveOrGenerateTraceId(request);
    request.setAttribute(TRACE_ID_ATTRIBUTE, traceId);
    response.setHeader(TRACE_ID_HEADER, traceId);

    filterChain.doFilter(request, response);
  }

  private String resolveOrGenerateTraceId(HttpServletRequest request) {
    String traceId = request.getHeader(TRACE_ID_HEADER);
    if (traceId == null || traceId.isBlank()) {
      traceId = request.getHeader(FALLBACK_HEADER);
    }
    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }
    return traceId;
  }
}
