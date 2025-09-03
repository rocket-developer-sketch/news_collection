package com.ddi.assessment.news.api.filter;

import com.ddi.assessment.news.api.auth.application.RefreshTokenService;
import com.ddi.assessment.news.api.security.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("인증 필터 테스트")
class JwtAuthenticationFilterTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    RefreshTokenService refreshTokenService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Authorization 헤더가 없을 때 SecurityContext 설정 안함")
    void testWithoutAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, refreshTokenService);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰 만료 일 때 빈 SecurityContext 반환")
    void testExpiredTokenClearsSecurityContext() throws Exception {
        String token = "expired-not-refreshed-access-dummy-test-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtTokenProvider.resolveAccessToken(any())).thenReturn(token);
        when(jwtTokenProvider.parseAndValidateAccessToken(token)).thenThrow(new JwtException("expired token"));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, refreshTokenService);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }
}
