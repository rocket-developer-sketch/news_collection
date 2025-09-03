package com.ddi.assessment.news.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobLoggingListener implements JobExecutionListener {
    Logger log = LoggerFactory.getLogger(JobLoggingListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting Job - JobName: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Finishing Job - JobName: {}, Status: {}", jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
    }
}
