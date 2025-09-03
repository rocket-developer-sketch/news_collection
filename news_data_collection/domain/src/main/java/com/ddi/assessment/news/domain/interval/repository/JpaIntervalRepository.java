package com.ddi.assessment.news.domain.interval.repository;

import com.ddi.assessment.news.domain.interval.entity.JpaInterval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaIntervalRepository extends JpaRepository<JpaInterval, Long> {
    Optional<JpaInterval> findByLabel(String label);
}
