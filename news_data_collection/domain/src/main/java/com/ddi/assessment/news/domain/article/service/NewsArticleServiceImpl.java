package com.ddi.assessment.news.domain.article.service;

import com.ddi.assessment.news.domain.article.entity.JpaNewsArticle;
import com.ddi.assessment.news.domain.article.repository.NewsArticleRepository;

import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;
import com.ddi.assessment.news.domain.article.vo.RegisterScheduleCommand;
import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import com.ddi.assessment.news.domain.collectrule.service.CollectionRuleService;
import com.ddi.assessment.news.domain.interval.service.IntervalService;
import com.ddi.assessment.news.domain.keyword.service.KeywordService;
import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import com.ddi.assessment.news.domain.site.service.NewsSiteService;
import com.ddi.assessment.news.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsArticleServiceImpl implements NewsArticleService {
    private final NewsArticleRepository newsArticleRepository;
    private final NewsSiteService newsSiteService;
    private final CollectionRuleService collectionRuleService;

    public NewsArticleServiceImpl(NewsArticleRepository newsArticleRepository, NewsSiteService newsSiteService, CollectionRuleService collectionRuleService) {
        this.newsArticleRepository = newsArticleRepository;
        this.newsSiteService = newsSiteService;
        this.collectionRuleService = collectionRuleService;
    }

    @Override
    public Set<String> findExistingNewsUrls(Long ruleId, Set<String> previewUrls) {
        return new HashSet<>(newsArticleRepository.findExistingUrls(ruleId, previewUrls));
    }

    @Override
    public void saveAllParsedNewsArticle(RegisterScheduleCommand command) {

        JpaNewsSite newsSite = newsSiteService.getSite(command.siteId());
        JpaCollectRule rule = collectionRuleService.getCollectConfig(command.ruleId());

        List<JpaNewsArticle> entities = command.articles().stream()
                .map(article -> new JpaNewsArticle(
                        newsSite,
                        rule,
                        command.keyword(),
                        article.newsUrl(),
                        article.title(),
                        article.content(),
                        article.publishedAt(),
                        article.newsId()
                ))
                .collect(Collectors.toList());

        newsArticleRepository.saveAll(entities);
    }

    @Override
    public Page<CollectedNewsArticle> getNewsArticles(int pageNo, int size) {
        Pageable pageable = PageRequest.of(pageNo -1, size);

        return newsArticleRepository.findNewsArticles(pageable);
    }

    @Override
    public Page<CollectedNewsArticle> getUserNewsArticles(Long userId, int pageNo, int size) {
        Pageable pageable = PageRequest.of(pageNo -1, size);

        return newsArticleRepository.findUserNewsArticles(userId, pageable);
    }

    @Transactional
    public int deleteArticlesByRuleId(Long ruleId) {
        return newsArticleRepository.deleteByJobConfigId(ruleId);
    }

}
