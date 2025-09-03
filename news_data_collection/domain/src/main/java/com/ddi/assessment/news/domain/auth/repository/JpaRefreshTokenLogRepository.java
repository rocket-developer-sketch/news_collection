package com.ddi.assessment.news.domain.auth.repository;

import com.ddi.assessment.news.domain.auth.entity.JpaRefreshTokenLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaRefreshTokenLogRepository extends JpaRepository<JpaRefreshTokenLog, Long> {

    @Query("""
        select r from JpaRefreshTokenLog r
        where r.userId = :userId
        and r.revoked = false
        order by r.issuedAt desc
        limit 1
    """)
    Optional<JpaRefreshTokenLog> findLatestActiveByUserId(String userId);

    @Query("""
        select case when (
            exists (
                select 1
                from JpaRefreshTokenLog rtl
                where rtl.userId = :userId
                  and rtl.tokenHash = :tokenHash
            )
        ) then true else false end
        from JpaRefreshTokenLog rtl2
    """)
    boolean existsToken(String userId, String tokenHash);

    @Modifying
    @Query("update JpaRefreshTokenLog r set r.revoked = true where r.userId = :userId and r.revoked = false")
    int revokeAllActiveTokenByUserId(@Param("userId") String userId);

    @Modifying
    @Query("update JpaRefreshTokenLog r set r.revoked = true where r.userId = :userId and r.tokenHash = :tokenHash")
    int revokeByUserIdAndTokenHash(@Param("userId") String userId, @Param("tokenHash")String tokenHash);

    @Modifying
    @Query("delete from JpaRefreshTokenLog r where r.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);

}

