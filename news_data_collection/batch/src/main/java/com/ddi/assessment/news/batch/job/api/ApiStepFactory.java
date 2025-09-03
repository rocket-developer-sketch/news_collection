package com.ddi.assessment.news.batch.job.api;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.batch.collector.CollectorDispatcher;
import com.ddi.assessment.news.batch.job.SaveTasklet;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Component
public class ApiStepFactory {
    private final JobRepository jobRepository;
    private final CollectorDispatcher dispatcher;
    private final NewsArticleService articleService;
    private final PlatformTransactionManager transactionManager;

    public ApiStepFactory(JobRepository jobRepository, CollectorDispatcher dispatcher,
                          NewsArticleService articleService,
                          @Qualifier("newsTransactionManager") PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.dispatcher = dispatcher;
        this.articleService = articleService;
        this.transactionManager = transactionManager;
    }
    public Step apiFetchStep(NewsCollectionJob job) {
        return new StepBuilder("apiFetchStep-" + job.getConfigId(), jobRepository)
                .tasklet(new ApiDetailFetchTasklet(job, dispatcher), transactionManager)
                .build();
    }

    public Step dedupStep(NewsCollectionJob job) {
        return new StepBuilder("apiDedupStep-" + job.getConfigId(), jobRepository)
                .tasklet(new ApiDuplicationTasklet(job, articleService), transactionManager)
                .build();
    }

    public Step saveStep(NewsCollectionJob job) {
        return new StepBuilder("apiSaveStep-" + job.getConfigId(), jobRepository)
                .tasklet(new SaveTasklet(job, articleService), transactionManager)
                .build();
    }

    // save 만 chunk

//    public Step saveSteps(NewsCollectionJob job) {
//        List<ParsedNewsArticle> articles = job.getParsedArticlesHolder().getArticles(); // 가공된 결과
//
//        return new StepBuilder("apiSaveStep-" + job.getConfigId(), jobRepository)
//                .<ParsedNewsArticle, ParsedNewsArticle>chunk(100, transactionManager)
//                .reader(new InMemoryListItemReader<>(articles))
//                .writer(items -> articleService.saveAll(items))  // 혹은 주입된 writer
//                .build();
//    }
}
