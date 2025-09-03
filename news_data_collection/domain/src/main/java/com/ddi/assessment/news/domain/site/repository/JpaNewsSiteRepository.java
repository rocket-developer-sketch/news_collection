package com.ddi.assessment.news.domain.site.repository;

import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaNewsSiteRepository extends JpaRepository<JpaNewsSite, Long> {
    Optional<JpaNewsSite> findById(Long id);
    Optional<JpaNewsSite> findBySiteName(String siteName);
    List<JpaNewsSite> findBySiteNameIn(Set<String> siteNames);
}
