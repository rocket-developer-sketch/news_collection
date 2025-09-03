package com.ddi.assessment.news.api.auth.application;

import com.ddi.assessment.news.api.auth.dto.*;

public interface RefreshTokenService {
    RefreshTokenStatusResponse isUserLatestRefreshTokenRevoked(RefreshTokenStatusRequest request);
    GetRefreshTokenResponse userLatestActiveRefreshToken(GetRefreshTokenRequest request);
    CreateRefreshTokenResponse createActiveRefreshTokenLog(CreateRefreshTokenRequest request);
    int revokeAllActiveTokenByUser(String userId);
    int revokeToken(RevokeRefreshTokenRequest request);
}
