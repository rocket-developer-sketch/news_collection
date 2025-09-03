package com.ddi.assessment.news.batch.job.crawler;

import com.ddi.assessment.news.batch.collector.CollectorDispatcher;
import com.ddi.assessment.news.batch.job.SaveTasklet;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class CrawlingStepFactory {

    private final JobRepository jobRepository;
    private final CollectorDispatcher dispatcher;
    private final NewsArticleService articleService;
    private final PlatformTransactionManager transactionManager;

    public CrawlingStepFactory(JobRepository jobRepository, CollectorDispatcher dispatcher,
                               NewsArticleService articleService,
                               @Qualifier("newsTransactionManager") PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.dispatcher = dispatcher;
        this.articleService = articleService;
        this.transactionManager = transactionManager;
    }

    public Step listFetchStep(NewsCollectionJob job) {
        return new StepBuilder("listFetchStep-" + job.getConfigId(), jobRepository)
                .tasklet(new ListFetchTasklet(job, dispatcher), transactionManager)
                .build();
    }

    // 마지막 db 저장할 때 유니크 키로 저장 불가하도록 작성해도 될 것 같다
    public Step dedupStep(NewsCollectionJob job) {
        return new StepBuilder("dedupStep-" + job.getConfigId(), jobRepository)
                .tasklet(new DeduplicationTasklet(job, articleService), transactionManager)
                .build();
    }

    public Step detailFetchStep(NewsCollectionJob job) {
        return new StepBuilder("detailFetchStep-" + job.getConfigId(), jobRepository)
                .tasklet(new DetailFetchTasklet(job, dispatcher), transactionManager)
                .build();
    }

    public Step saveStep(NewsCollectionJob job) {
        return new StepBuilder("saveStep-" + job.getConfigId(), jobRepository)
                .tasklet(new SaveTasklet(job, articleService), transactionManager)
                .build();
    }

}
