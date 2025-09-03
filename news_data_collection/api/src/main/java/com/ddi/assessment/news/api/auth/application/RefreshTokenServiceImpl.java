package com.ddi.assessment.news.api.auth.application;

import com.ddi.assessment.news.api.auth.dto.*;
import com.ddi.assessment.news.domain.auth.vo.*;
import com.ddi.assessment.news.domain.auth.service.RefreshTokenLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    RefreshTokenLogService refreshTokenLogService;

    @Override
    public RefreshTokenStatusResponse isUserLatestRefreshTokenRevoked(RefreshTokenStatusRequest request) {
        return new RefreshTokenStatusResponse(refreshTokenLogService.isUserLatestRefreshTokenRevoked(new ActiveRefreshTokenQuery(request.userId())));
    }

    public GetRefreshTokenResponse userLatestActiveRefreshToken(GetRefreshTokenRequest request) {
        ActiveRefreshToken activeRefreshToken = refreshTokenLogService.userActiveLatestRefreshToken(new ActiveRefreshTokenQuery(request.userId()));
        return new GetRefreshTokenResponse(activeRefreshToken.tokenHash());
    }

    @Override
    public CreateRefreshTokenResponse createActiveRefreshTokenLog(CreateRefreshTokenRequest request) {
        ActiveRefreshToken activeRefreshToken = refreshTokenLogService.createActiveRefreshTokenLog(new ActiveRefreshTokenCommand(request.userId(), request.issuedAt(), request.expiredAt(), request.tokenHash()));
        return new CreateRefreshTokenResponse(activeRefreshToken.userId(), activeRefreshToken.tokenHash());
    }

    @Override
    public int revokeAllActiveTokenByUser(String userId) {
        return refreshTokenLogService.revokeAllActiveTokenByUser(userId);
    }

    @Override
    public int revokeToken(RevokeRefreshTokenRequest request) {
        return refreshTokenLogService.revokeTokenByUser(new RevokeRefreshTokenCommand(request.userId(), request.currentTokenHash()));
    }
}
