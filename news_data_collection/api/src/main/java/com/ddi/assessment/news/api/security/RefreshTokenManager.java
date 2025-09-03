package com.ddi.assessment.news.api.security;

import com.ddi.assessment.news.api.auth.application.RefreshTokenService;
import com.ddi.assessment.news.api.auth.dto.*;
import com.ddi.assessment.news.domain.auth.exception.RefreshTokenRotationException;
import com.ddi.assessment.news.domain.user.service.UserService;
import com.ddi.assessment.news.domain.user.vo.TokenOwner;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenManager {

    private static final SecureRandom RAND = new SecureRandom();
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtProperties props;

    public RefreshTokenManager(RefreshTokenService refreshTokenService, UserService userService, JwtProperties props) {
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.props = props;
    }

    private String newRawToken() {
        byte[] buf = new byte[96];
        RAND.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    public String create(String userId) {
        String raw = newRawToken();

        refreshTokenService.createActiveRefreshTokenLog(new CreateRefreshTokenRequest(userId, LocalDateTime.now(),
                LocalDateTime.now().plus(Duration.ofMillis(props.getRefreshTokenValidity())), HashUtils.hash(raw)));

        return raw;
    }

    // todo 멀티 디바이스 환경은?
    public TokenRotate rotate(String presentedRaw, String userId) {
        TokenOwner owner = userService.findTokenOwner(userId);

        // 현재 가장 최신의 유효한 토큰 로그 조회
        GetRefreshTokenResponse active = refreshTokenService.userLatestActiveRefreshToken(
                new GetRefreshTokenRequest(userId)
        );
        String currentHash = active.tokenHash();

        // 토큰 불일치 → 재사용 감지 → 전체 revoke
        if (!HashUtils.matches(presentedRaw, currentHash)) {
            refreshTokenService.revokeAllActiveTokenByUser(userId);
            throw new RefreshTokenRotationException("리프레시 토큰 불일치 또는 재사용 탐지");
        }

        // 새로운 토큰 발급
        String newRaw = newRawToken();
        String newHash = HashUtils.hash(newRaw);

        // 기존 토큰 revoke + 새로운 토큰 저장
        refreshTokenService.revokeToken(new RevokeRefreshTokenRequest(userId, currentHash));
        refreshTokenService.createActiveRefreshTokenLog(new CreateRefreshTokenRequest(
                userId,
                LocalDateTime.now(),
                LocalDateTime.now().plus(Duration.ofMillis(props.getRefreshTokenValidity())),
                newHash
        ));

        return new TokenRotate(owner.userId(), newHash);

    }

    public void revokeAll(String userId) {
        refreshTokenService.revokeAllActiveTokenByUser(userId);
    }

}
