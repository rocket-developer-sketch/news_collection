package com.ddi.assessment.news.domain.article.repository;

import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsArticleCustomRepository {
    Page<CollectedNewsArticle> findNewsArticles(Pageable pageable);
    Page<CollectedNewsArticle> findUserNewsArticles(Long userId, Pageable pageable);
}
