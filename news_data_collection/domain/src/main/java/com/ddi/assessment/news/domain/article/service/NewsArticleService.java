package com.ddi.assessment.news.domain.article.service;

import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;
import com.ddi.assessment.news.domain.article.vo.RegisterScheduleCommand;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface NewsArticleService {
    Set<String> findExistingNewsUrls(Long configId, Set<String> previewUrls);
    Page<CollectedNewsArticle> getNewsArticles(int pageNo, int size);
    Page<CollectedNewsArticle> getUserNewsArticles(Long userId, int pageNo, int size);
    void saveAllParsedNewsArticle(RegisterScheduleCommand command);
    int deleteArticlesByRuleId(Long ruleId);
}
