package com.ddi.assessment.news.batch.job.daum;

import com.ddi.assessment.news.batch.job.NewsJobStrategy;
import com.ddi.assessment.news.batch.job.crawler.CrawlingStepFactory;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
public class DaumJobStrategy implements NewsJobStrategy {

    private final CrawlingStepFactory stepFactory;
    private final JobRepository jobRepository;
    private final JobExecutionListener jobLoggingListener;

    public DaumJobStrategy(CrawlingStepFactory stepFactory,
                           JobRepository jobRepository, JobExecutionListener jobLoggingListener
    ) {
        this.stepFactory = stepFactory;
        this.jobRepository = jobRepository;
        this.jobLoggingListener = jobLoggingListener;
    }

    @Override
    public String getNewsSiteName() {
        return "DAUM";
    }

    @Override
    public Job buildJob(NewsCollectionJob job) {
        return new JobBuilder("crawlJob - " + job.getNewsSite() + " - " + job.getConfigId(), jobRepository)
                .listener(jobLoggingListener)
                .start(stepFactory.listFetchStep(job))
                .next(stepFactory.dedupStep(job))
                .next(stepFactory.detailFetchStep(job))
                .next(stepFactory.saveStep(job))
                .build();
    }
}
