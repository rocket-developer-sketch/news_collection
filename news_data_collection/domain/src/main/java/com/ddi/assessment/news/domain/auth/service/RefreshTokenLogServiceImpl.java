package com.ddi.assessment.news.domain.auth.service;

import com.ddi.assessment.news.domain.auth.exception.RefreshTokenRotationException;
import com.ddi.assessment.news.domain.auth.vo.*;
import com.ddi.assessment.news.domain.auth.entity.JpaRefreshTokenLog;
import com.ddi.assessment.news.domain.auth.repository.JpaRefreshTokenLogRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@Service
public class RefreshTokenLogServiceImpl implements RefreshTokenLogService {
    @Autowired
    JpaRefreshTokenLogRepository jpaRefreshTokenLogRepository;

    @Override
    public Boolean isUserLatestRefreshTokenRevoked(ActiveRefreshTokenQuery query) throws RefreshTokenRotationException {
        Optional<JpaRefreshTokenLog> logOrEmpty = jpaRefreshTokenLogRepository.findLatestActiveByUserId(query.userId());

        if(logOrEmpty.isEmpty()) {
            return true;
        }

        return logOrEmpty.get().getRevoked();
    }

    @Override
    public ActiveRefreshToken userActiveLatestRefreshToken(ActiveRefreshTokenQuery query) {
        Optional<JpaRefreshTokenLog> logOrEmpty = jpaRefreshTokenLogRepository.findLatestActiveByUserId(query.userId());

        if(logOrEmpty.isEmpty()) {
            return ActiveRefreshToken.empty();
        }

        return new ActiveRefreshToken(query.userId(), logOrEmpty.get().getTokenHash());
    }

    @Override
    public ActiveRefreshToken createActiveRefreshTokenLog(ActiveRefreshTokenCommand command) {
        JpaRefreshTokenLog saved = jpaRefreshTokenLogRepository.save(new JpaRefreshTokenLog(command.userId(), command.tokenHash(), command.issuedAt(), command.expiredAt(), false));

        return new ActiveRefreshToken(saved.getUserId(), saved.getTokenHash());

    }

    @Override
    public int revokeAllActiveTokenByUser(String userId) {
        return jpaRefreshTokenLogRepository.revokeAllActiveTokenByUserId(userId);
    }

    @Override
    public int revokeTokenByUser(RevokeRefreshTokenCommand command) {
        return jpaRefreshTokenLogRepository.revokeByUserIdAndTokenHash(command.userId(), command.currentHashToken());
    }

}
