package com.ddi.assessment.news.batch.job.api;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.CollectorDispatcher;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

public class ApiDetailFetchTasklet implements Tasklet {

    private final NewsCollectionJob job;
    private final CollectorDispatcher dispatcher;

    public ApiDetailFetchTasklet(NewsCollectionJob job, CollectorDispatcher dispatcher) {
        this.job = job;
        this.dispatcher = dispatcher;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<ParsedNewsArticle> articles = dispatcher.collectArticles(job);

        job.getParsedArticlesHolder().setArticles(articles);

        return RepeatStatus.FINISHED;
    }
}
