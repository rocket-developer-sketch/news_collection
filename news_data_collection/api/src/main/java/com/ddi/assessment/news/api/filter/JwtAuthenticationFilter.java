package com.ddi.assessment.news.api.filter;

import com.ddi.assessment.news.api.auth.application.RefreshTokenService;
import com.ddi.assessment.news.api.auth.dto.RefreshTokenStatusRequest;
import com.ddi.assessment.news.api.auth.dto.RefreshTokenStatusResponse;
import com.ddi.assessment.news.api.auth.exception.RevokedTokenException;
import com.ddi.assessment.news.api.security.JwtTokenProvider;
import com.ddi.assessment.news.api.security.JwtUserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = jwtTokenProvider.resolveAccessToken(request);

        if (token != null) {
            try {
                Jws<Claims> jws = jwtTokenProvider.parseAndValidateAccessToken(token);

                Claims claims = jws.getPayload();

                // 사용자 관리 번호
                Long id = Long.parseLong(claims.getSubject());
                // 사용자 로그인 아이디
                String userId = claims.get("userId", String.class);

                RefreshTokenStatusResponse status = refreshTokenService.isUserLatestRefreshTokenRevoked(new RefreshTokenStatusRequest(userId));
                if (status.revoked()) {
                    throw new RevokedTokenException("만료된 토큰 입니다.");
                }

                Object rolesObj = claims.get("roles");
                String[] roles = rolesObj instanceof List
                        ? ((List<?>) rolesObj).stream().map(Object::toString).toArray(String[]::new)
                        : rolesObj instanceof String s ? s.split(",") : new String[0];

                List<SimpleGrantedAuthority> authorities =
                        Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();

                JwtUserDetail principal = new JwtUserDetail(id, userId);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // JWT가 만료되었거나 잘못된 경우
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
