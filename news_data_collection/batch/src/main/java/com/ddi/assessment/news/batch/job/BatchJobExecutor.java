package com.ddi.assessment.news.batch.job;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
public class BatchJobExecutor {
    private final JobLauncher jobLauncher;
    private final NewsJobFactory newsJobFactory;
    private final JobExplorer jobExplorer;

    public BatchJobExecutor(JobLauncher jobLauncher, NewsJobFactory newsJobFactory, JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.newsJobFactory = newsJobFactory;
        this.jobExplorer = jobExplorer;
    }

    public void execute(NewsCollectionJob createJob) throws Exception {

        Job job = newsJobFactory.build(createJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addLong("configId", createJob.getConfigId())
                .toJobParameters();

        jobLauncher.run(job, jobParameters);
    }
}
