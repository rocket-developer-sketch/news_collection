package com.ddi.assessment.news.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenProvider {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtProperties props;
    private final SecretKey secretKey;
    private final RefreshTokenManager refreshTokenManager;

    public JwtTokenProvider(JwtProperties props, RefreshTokenManager refreshTokenManager) {
        this.props = props;
        this.secretKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshTokenManager = refreshTokenManager;
    }

    public TokenPair issueTokens(Long id, String userId, String[] roles) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessExp = now.plus(Duration.ofMillis(props.getAccessTokenValidity()));
        String jti = UUID.randomUUID().toString();
        String access = Jwts.builder()
                .setSubject(String.valueOf(id))
                .setExpiration(toDate(accessExp))
                .setIssuedAt(toDate(now))
                .setId(jti)
                .claim("userId", userId)
                .claim("roles", roles)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshRaw = refreshTokenManager.create(userId);

        LocalDateTime refreshExp = now.plus(Duration.ofMillis(props.getRefreshTokenValidity()));

        return new TokenPair(access, refreshRaw, toEpochMillis(accessExp), toEpochMillis(refreshExp));
    }

    public TokenPair refresh(String userId, String[] roles, String rawRefresh) {

        TokenRotate newRefresh = refreshTokenManager.rotate(rawRefresh, userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessExp = now.plus(Duration.ofMillis(props.getAccessTokenValidity()));
        String access = Jwts.builder()
                .setExpiration(toDate(accessExp))
                .setIssuedAt(toDate(now))
                .setId(UUID.randomUUID().toString())
                .setSubject(String.valueOf(newRefresh.userId()))
                .claim("userId", userId)
                .claim("roles", roles)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        LocalDateTime refreshExp = now.plus(Duration.ofMillis(props.getRefreshTokenValidity()));

        return new TokenPair(access, newRefresh.tokenHash(), toEpochMillis(accessExp), toEpochMillis(refreshExp));
    }

    public Jws<Claims> parseAndValidateAccessToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length()).trim();
        }

        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie c : request.getCookies()) {
            if (props.getCookie().getRefreshCookieName().equals(c.getName())) {
                return c.getValue();
            }
        }

        return null;
    }

    private Date toDate(LocalDateTime ldt) {
        //return Date.from(ldt.atZone(ZoneId.of("UTC")).toInstant());
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

    }

    private long toEpochMillis(LocalDateTime ldt) {
        //return ldt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
