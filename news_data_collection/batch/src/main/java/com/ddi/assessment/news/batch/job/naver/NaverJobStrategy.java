package com.ddi.assessment.news.batch.job.naver;

import com.ddi.assessment.news.batch.job.NewsJobStrategy;
import com.ddi.assessment.news.batch.job.api.ApiStepFactory;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
public class NaverJobStrategy implements NewsJobStrategy {
    private final ApiStepFactory stepFactory;
    private final JobRepository jobRepository;
    private final JobExecutionListener jobLoggingListener;

    public NaverJobStrategy(ApiStepFactory stepFactory,
                            JobRepository jobRepository, JobExecutionListener jobLoggingListener) {
        this.stepFactory = stepFactory;
        this.jobRepository = jobRepository;
        this.jobLoggingListener = jobLoggingListener;
    }

    @Override
    public String getNewsSiteName() {
        return "NAVER";
    }

    @Override
    public Job buildJob(NewsCollectionJob job) {
        return new JobBuilder("apiJob - " + job.getNewsSite() + " - " + job.getConfigId(), jobRepository)
                .listener(jobLoggingListener)
                .start(stepFactory.apiFetchStep(job))
                .next(stepFactory.dedupStep(job))
                .next(stepFactory.saveStep(job))
                .build();
    }

}
