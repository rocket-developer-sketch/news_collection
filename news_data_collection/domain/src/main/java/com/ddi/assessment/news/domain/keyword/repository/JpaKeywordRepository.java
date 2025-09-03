package com.ddi.assessment.news.domain.keyword.repository;

import com.ddi.assessment.news.domain.keyword.entity.JpaKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaKeywordRepository extends JpaRepository<JpaKeyword, Long> {
    Optional<JpaKeyword> findByWord(String keyword);
    List<JpaKeyword> findByWordIn(Set<String> words);
}
