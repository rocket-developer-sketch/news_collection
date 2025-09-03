package com.ddi.assessment.news.batch.job;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;
import com.ddi.assessment.news.domain.article.vo.RegisterScheduleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

public class SaveTasklet implements Tasklet {
    private static final Logger log = LoggerFactory.getLogger(SaveTasklet.class);

    private static final int BATCH_SIZE = 300;

    private final NewsCollectionJob job;
    private final NewsArticleService articleService;

    public SaveTasklet(NewsCollectionJob job, NewsArticleService articleService) {
        this.job = job;
        this.articleService = articleService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<ParsedNewsArticle> articles = job.getParsedArticlesHolder().getArticles();
        if (articles == null || articles.isEmpty()) {
            log.debug("Empty Articles To Save");
            return RepeatStatus.FINISHED;
        }

        int total = articles.size();
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);

            List<ParsedNewsArticle> sublist = articles.subList(i, end);

            try {
                articleService.saveAllParsedNewsArticle(toCommand(sublist));
            } catch (Exception e) {
                log.error("Failed To Save News Articles: " + e.getMessage());
            }
        }

        return RepeatStatus.FINISHED;
    }

    private RegisterScheduleCommand toCommand(List<ParsedNewsArticle> sublist) {
        List<CollectedNewsArticle> collectedArticles = sublist.stream()
                .map(article -> new CollectedNewsArticle(
                        article.getNewsId(),
                        article.getKeyword(),
                        article.getSiteName(),
                        article.getNewsUrl(),
                        article.getTitle(),
                        article.getContent(),
                        article.getPublishedAt()
                ))
                .toList();

        return new RegisterScheduleCommand (
                job.getConfigId(),
                job.getKeywordId(),
                job.getSiteId(),
                job.getKeyword(),
                job.getNewsSite(),
                job.getSiteUrl(),
                job.getCronExpression(),
                collectedArticles
        );

    }
}
