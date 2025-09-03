package com.ddi.assessment.news.api.article.application;

import com.ddi.assessment.news.api.dto.PageResponse;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class CollectedNewsArticleServiceImpl implements CollectedNewsArticleService {
    private final NewsArticleService newsArticleService;

    public CollectedNewsArticleServiceImpl(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
    }

    @Override
    public PageResponse<CollectedNewsArticle> getNewsArticles(int pageNo, int size) {

        Page<CollectedNewsArticle> allConfigs = newsArticleService.getNewsArticles(pageNo, size);

        return new PageResponse<>(
                allConfigs.getContent(),
                allConfigs.getNumber() + 1,
                allConfigs.getSize(),
                allConfigs.getTotalElements(),
                allConfigs.getTotalPages(),
                allConfigs.isLast()
        );

    }

    @Override
    public PageResponse<CollectedNewsArticle> getUserNewsArticles(Long userId, int pageNo, int size) {

        Page<CollectedNewsArticle> allConfigs = newsArticleService.getUserNewsArticles(userId, pageNo, size);

        return new PageResponse<>(
                allConfigs.getContent(),
                allConfigs.getNumber() + 1,
                allConfigs.getSize(),
                allConfigs.getTotalElements(),
                allConfigs.getTotalPages(),
                allConfigs.isLast()
        );
    }
}
