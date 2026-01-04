package com.hms.gateway.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, wrapper);
        } finally {
            long ms = System.currentTimeMillis() - start;
            int status = wrapper.getStatus();
            String method = request.getMethod();
            String path = request.getRequestURI();
            String qs = request.getQueryString();
            log.info("{} {}{} -> {} ({} ms)",
                    method,
                    path,
                    (qs != null ? "?" + qs : ""),
                    status,
                    ms
            );
            wrapper.copyBodyToResponse();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator");
    }
}
