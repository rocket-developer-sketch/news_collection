package com.ddi.assessment.news.batch.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class InternalApiCallFilter extends OncePerRequestFilter {

    private final String internalSecret;

    public InternalApiCallFilter(String internalSecret) {
        this.internalSecret = internalSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // 내부 보호 대상 경로만 적용
        if (path.startsWith("/schedule")) {
            String header = request.getHeader("X-Internal-Secret");
            if (!internalSecret.equals(header)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Forbidden");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}