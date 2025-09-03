package com.ddi.assessment.news.batch.job.api;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiDuplicationTasklet implements Tasklet {
    Logger log = LoggerFactory.getLogger(ApiDuplicationTasklet.class);

    private final NewsCollectionJob job;
    private final NewsArticleService articleService;

    public ApiDuplicationTasklet(NewsCollectionJob job, NewsArticleService articleService) {
        this.job = job;
        this.articleService = articleService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        List<ParsedNewsArticle> articles = job.getParsedArticlesHolder().getArticles();
        if (articles == null || articles.isEmpty()) {
            log.debug("Empty Articles To Process");
            return RepeatStatus.FINISHED;
        }

        Set<String> newsUrls = articles.stream()
                .map(ParsedNewsArticle::getNewsUrl)
                .collect(Collectors.toSet());

        Set<String> existingUrls = new HashSet<>(articleService.findExistingNewsUrls(job.getConfigId(), newsUrls));

        List<ParsedNewsArticle> deduped = articles.stream()
                .filter(article -> !existingUrls.contains(article.getNewsUrl()))
                .toList();

        job.getParsedArticlesHolder().setArticles(deduped);

        return RepeatStatus.FINISHED;
    }
}
