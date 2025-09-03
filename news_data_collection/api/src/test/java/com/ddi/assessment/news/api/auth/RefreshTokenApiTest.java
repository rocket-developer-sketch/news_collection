package com.ddi.assessment.news.api.auth;

import com.ddi.assessment.news.api.auth.application.AuthService;
import com.ddi.assessment.news.api.auth.application.RefreshTokenService;
import com.ddi.assessment.news.api.auth.controller.AuthController;
import com.ddi.assessment.news.api.security.JwtTokenProvider;
import com.ddi.assessment.news.api.security.RefreshTokenManager;
import com.ddi.assessment.news.api.security.TokenPair;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("refresh token 재발급 테스트")
public class RefreshTokenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RefreshTokenManager refreshTokenManager;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AuthService authService;

    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    @Nested
    @DisplayName("RefreshToken 없을 때")
    class NoCookie {

        @Test
        @DisplayName("401 Unauthorized")
        void testNoRefreshTokenCookie() throws Exception {
            mockMvc.perform(post("/auth/refresh")
                            .header("X-User-Id", "testUser1"))
                    .andExpect(status().isUnauthorized());
        }

    }

    @Nested
    @DisplayName("유효한 RefreshToken 요청 시")
    class ValidToken {

        @Test
        @DisplayName("access/refresh 토큰 재발급 성공")
        void testValidRefreshToken() throws Exception {
            TokenPair tokens = new TokenPair("new-access-token", "new-refresh", 123546872L, 687551725L);

            when(jwtTokenProvider.refresh(any(), any(), any()))
                    .thenReturn(tokens);

            MockCookie cookie = new MockCookie(REFRESH_COOKIE_NAME, "valid-refresh-token");

            mockMvc.perform(post("/auth/refresh")
                            .cookie(cookie)
                            .header("X-User-Id", "testUser1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer new-access-token"))
                    .andExpect(jsonPath("$.data.accessTokenExpiresInSec").value(123546872L));
        }

    }
}
