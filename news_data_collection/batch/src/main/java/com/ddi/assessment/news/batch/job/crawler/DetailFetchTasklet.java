package com.ddi.assessment.news.batch.job.crawler;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.CollectorDispatcher;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

public class DetailFetchTasklet implements Tasklet {
    Logger log = LoggerFactory.getLogger(DetailFetchTasklet.class);

    private final NewsCollectionJob job;
    private final CollectorDispatcher dispatcher;

    public DetailFetchTasklet(NewsCollectionJob job, CollectorDispatcher dispatcher) {
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
