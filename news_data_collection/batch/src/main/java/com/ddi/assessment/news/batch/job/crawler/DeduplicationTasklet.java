package com.ddi.assessment.news.batch.job.crawler;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsPreview;

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

public class DeduplicationTasklet implements Tasklet {
    private static final Logger log = LoggerFactory.getLogger(DeduplicationTasklet.class);

    private final NewsCollectionJob job;
    private final NewsArticleService articleService;

    public DeduplicationTasklet(NewsCollectionJob job, NewsArticleService articleService) {
        this.job = job;
        this.articleService = articleService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<ParsedNewsPreview> previews = job.getParsedArticlesHolder().getPreviews();

        Set<String> previewUrls = previews.stream()
                .map(ParsedNewsPreview::getUrl)
                .collect(Collectors.toSet());

        Set<String> existingUrls = new HashSet<>(articleService.findExistingNewsUrls(job.getConfigId(), previewUrls));

        List<ParsedNewsPreview> deduped = previews.stream()
                .filter(preview -> !existingUrls.contains(preview.getUrl()))
                .toList();

        job.getParsedArticlesHolder().setPreviews(deduped);

        return RepeatStatus.FINISHED;
    }
}
