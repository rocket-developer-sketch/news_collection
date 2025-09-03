package com.ddi.assessment.news.api.auth.controller;

import com.ddi.assessment.news.api.dto.ResultResponse;
import com.ddi.assessment.news.api.auth.application.RefreshTokenService;
import com.ddi.assessment.news.api.auth.application.AuthService;
import com.ddi.assessment.news.api.auth.dto.AuthTokenResponse;
import com.ddi.assessment.news.api.auth.dto.LoginUserRequest;
import com.ddi.assessment.news.api.auth.dto.LoginUserResponse;
import com.ddi.assessment.news.api.auth.dto.RegisterUserRequest;
import com.ddi.assessment.news.api.security.JwtProperties;
import com.ddi.assessment.news.api.security.JwtTokenProvider;
import com.ddi.assessment.news.api.security.TokenPair;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    public AuthController(JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties, RefreshTokenService refreshTokenService, AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterUserRequest request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<ResultResponse<AuthTokenResponse>> login(@RequestBody LoginUserRequest request) {

        // todo admin 기능 필요하면, 관리자 로그인 로그 정보도 남겨야 함
        // adminuser 테이블 확인
        // String role = adminUserRepository.existsByUserId(user.getId())
        //        ? "ROLE_ADMIN"
        //        : "ROLE_USER";
        //TokenPair pair = jwtProvider.issueTokens(userId, List.of(role));

        LoginUserResponse loginUserResponse = authService.login(request);

        String[] roles = new String[]{"ROLE_USER"};
        TokenPair pair = jwtTokenProvider.issueTokens(loginUserResponse.id(), loginUserResponse.userId(), roles);

        ResponseCookie cookie = buildRefreshCookie(pair.rawRefresh(), pair.refreshExpEpoc());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + pair.access())
                .body(ResultResponse.success(new AuthTokenResponse(pair.accessExpEpoc())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResultResponse<AuthTokenResponse>> refresh(
            @CookieValue(name = "refresh_token") String refreshToken,
            @RequestHeader(name = "X-User-Id") String userId,
            HttpServletRequest request) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(ResultResponse.failure("리프레시 토큰 없음"));
        }

        String[] roles = new String[]{"ROLE_USER"};

        TokenPair pair = jwtTokenProvider.refresh(userId, roles, refreshToken);
        ResponseCookie cookie = buildRefreshCookie(pair.rawRefresh(), pair.refreshExpEpoc());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + pair.access())
                .body(ResultResponse.success(new AuthTokenResponse(pair.accessExpEpoc())));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-User-Id") String userId) {

        refreshTokenService.revokeAllActiveTokenByUser(userId);

        // 쿠키 제거
        ResponseCookie cookie = ResponseCookie
                .from(jwtProperties.getCookie().getRefreshCookieName(), "")
                .httpOnly(true)
                .secure(jwtProperties.getCookie().isSecure())
                .path(jwtProperties.getCookie().getPath())
                .maxAge(0)
                .domain(jwtProperties.getCookie().getDomain())
                .sameSite(jwtProperties.getCookie().getSameSite())
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseCookie buildRefreshCookie(String value, long refreshExpEpochMillis) {
        ZonedDateTime refreshExpKst = Instant.ofEpochMilli(refreshExpEpochMillis)
                .atZone(ZoneId.of("Asia/Seoul"));

        ZonedDateTime nowKst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        long maxAgeSeconds = Duration.between(nowKst, refreshExpKst).getSeconds();
        if (maxAgeSeconds <= 0) {
            maxAgeSeconds = 0; // 쿠키 삭제
        }

        return buildCookie(value, maxAgeSeconds);
    }

    private ResponseCookie buildCookie(String value, long maxAgeSeconds) {
        return ResponseCookie
                .from(jwtProperties.getCookie().getRefreshCookieName(), value)
                .httpOnly(true)
                .secure(jwtProperties.getCookie().isSecure())
                .path(jwtProperties.getCookie().getPath())
                .maxAge(maxAgeSeconds)
                .domain(jwtProperties.getCookie().getDomain())
                .sameSite(jwtProperties.getCookie().getSameSite())
                .build();
    }

}
