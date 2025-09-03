package com.ddi.assessment.news.domain.article.repository;

import com.ddi.assessment.news.domain.article.entity.JpaNewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface NewsArticleRepository extends JpaRepository<JpaNewsArticle, Long>, NewsArticleCustomRepository {
    @Query("""
        SELECT a.newsUrl FROM JpaNewsArticle a
        WHERE a.collectRule.id = :ruleId AND a.newsUrl IN :urls 
    """)
    List<String> findExistingUrls(@Param("ruleId") Long ruleId, @Param("urls") Set<String> urls);

    @Modifying
    @Query("DELETE FROM JpaNewsArticle a WHERE a.collectRule.id = :ruleId")
    int deleteByJobConfigId(@Param("ruleId") Long ruleId);

}
