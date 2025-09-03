package com.ddi.assessment.news.batch.job.crawler;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsPreview;
import com.ddi.assessment.news.batch.collector.CollectorDispatcher;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

// 뉴스 리스트 크롤링
public class ListFetchTasklet implements Tasklet {

    private final NewsCollectionJob job;
    private final CollectorDispatcher dispatcher;

    public ListFetchTasklet(NewsCollectionJob job, CollectorDispatcher dispatcher) {
        this.job = job;
        this.dispatcher = dispatcher;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<ParsedNewsPreview> previews = dispatcher.collectPreviews(job);

        job.getParsedArticlesHolder().setPreviews(previews);

        return RepeatStatus.FINISHED;
    }
}
