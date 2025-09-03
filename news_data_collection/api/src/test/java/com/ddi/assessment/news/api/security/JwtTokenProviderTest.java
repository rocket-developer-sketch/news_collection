package com.ddi.assessment.news.api.security;

import com.ddi.assessment.news.api.auth.application.RefreshTokenService;
import com.ddi.assessment.news.domain.user.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("🧪 JWT 토큰 유틸 단위 테스트")
class JwtTokenProviderTest {

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    UserService userService;

    JwtTokenProvider jwtTokenProvider;

    JwtProperties props;

    private final String secret = "my-super-secret-key-for-jwt-test-purpose-1234567890";
    private final Key secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        props = new JwtProperties();
        props.setSecret(secret);
        props.setAccessTokenValidity(60_000L);
        props.setRefreshTokenValidity(600_000L);

        JwtCookieProperties cookieProps = new JwtCookieProperties();
        cookieProps.setRefreshCookieName("refresh-token");

        props.setCookie(cookieProps);

        RefreshTokenManager manager = new RefreshTokenManager(refreshTokenService, userService, props);
        jwtTokenProvider = new JwtTokenProvider(props, manager);
    }

    @Test
    @DisplayName("access/refresh 토큰 발급 시 claim과 만료 시간이 포함")
    void testIssueTokens() {
        TokenPair pair = jwtTokenProvider.issueTokens(1L, "testUser1", new String[]{"ROLE_USER"});

        assertThat(pair.access()).isNotBlank();
        assertThat(pair.rawRefresh()).isNotBlank();
        assertThat(pair.accessExpEpoc()).isPositive();
        assertThat(pair.refreshExpEpoc()).isPositive();
    }

    @Test
    @DisplayName("accessToken 파싱 후 subject와 roles 정상 확인")
    void testParseAndValidateAccessToken() {
        String[] roles = {"ROLE_USER"};
        TokenPair pair = jwtTokenProvider.issueTokens(1L, "testUser1", roles);

        Jws<Claims> parsed = jwtTokenProvider.parseAndValidateAccessToken(pair.access());

        Object sub = parsed.getPayload().getSubject();
        assertThat(Long.parseLong(sub.toString())).isEqualTo(1L);
        assertThat(parsed.getPayload().get("roles")).asList().contains("ROLE_USER");
    }

    @Test
    @DisplayName("Authorization 헤더에서 Bearer 토큰 찾기")
    void testResolveAccessToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer authrizationheadercontainssecretkey");

        String token = jwtTokenProvider.resolveAccessToken(request);

        assertThat(token).isEqualTo("authrizationheadercontainssecretkey");
    }

    @Test
    @DisplayName("쿠키에서 refreshToken 추출 가능")
    void testResolveRefreshToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("refresh-token", "refresh-token-value");
        request.setCookies(cookie);

        String token = jwtTokenProvider.resolveRefreshToken(request);

        assertThat(token).isEqualTo("refresh-token-value");
    }

    @Test
    @DisplayName("accessToken 만료 되면 JwtException")
    void testExpiredAccessTokenThrowsException() {
        String expiredToken = createExpiredAccessToken("testUser1");

        assertThatThrownBy(() -> {
            jwtTokenProvider.parseAndValidateAccessToken(expiredToken);
        }).isInstanceOf(JwtException.class);
    }

    // 테스트용 만료된 토큰 생성
    private String createExpiredAccessToken(String subject) {
        LocalDateTime issuedAt = LocalDateTime.now().minusHours(12);
        LocalDateTime expiredAt = issuedAt.plusMinutes(1);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(toDate(issuedAt))
                .setExpiration(toDate(expiredAt))
                .claim("roles", new String[]{"ROLE_USER"})
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 시간 기준은 UTC+0
    private Date toDate(LocalDateTime ldt) {
        //return Date.from(ldt.atZone(ZoneId.of("UTC")).toInstant());
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
