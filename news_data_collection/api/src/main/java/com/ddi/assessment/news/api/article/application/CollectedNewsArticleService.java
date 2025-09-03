package com.ddi.assessment.news.api.article.application;

import com.ddi.assessment.news.api.dto.PageResponse;
import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;

public interface CollectedNewsArticleService {
    PageResponse<CollectedNewsArticle> getNewsArticles(int pageNo, int size);
    PageResponse<CollectedNewsArticle> getUserNewsArticles(Long userId, int pageNo, int size);
}
