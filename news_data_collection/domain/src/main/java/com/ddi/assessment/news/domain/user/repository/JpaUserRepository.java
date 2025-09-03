package com.ddi.assessment.news.domain.user.repository;

import com.ddi.assessment.news.domain.user.entity.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<JpaUser, Long> {
    Optional<JpaUser> findByUserId(String userId);
    Optional<JpaUser> findByEmail(String email);
}
