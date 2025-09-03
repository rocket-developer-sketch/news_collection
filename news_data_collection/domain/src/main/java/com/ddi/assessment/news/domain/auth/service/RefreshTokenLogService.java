package com.ddi.assessment.news.domain.auth.service;

import com.ddi.assessment.news.domain.auth.vo.*;

public interface RefreshTokenLogService {
    Boolean isUserLatestRefreshTokenRevoked(ActiveRefreshTokenQuery query);
    ActiveRefreshToken userActiveLatestRefreshToken(ActiveRefreshTokenQuery activeRefreshToken);
    ActiveRefreshToken createActiveRefreshTokenLog(ActiveRefreshTokenCommand command);
    int revokeAllActiveTokenByUser(String userId);
    int revokeTokenByUser(RevokeRefreshTokenCommand revokeRefreshTokenCommand);
}
