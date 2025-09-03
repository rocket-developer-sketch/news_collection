package com.ddi.assessment.news.batch.job;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class QuartzNewsJob implements Job {

    @Autowired
    private BatchJobExecutor batchJobExecutor;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();

        Long configId = dataMap.getLong("configId");
        Long keywordId = dataMap.getLong("keywordId");
        Long siteId = dataMap.getLong("siteId");
        String keyword = dataMap.getString("keyword");
        String siteName= dataMap.getString("siteName");
        String siteUrl = dataMap.getString("siteUrl");
        String cronExpression = dataMap.getString("cronExpression");

        NewsCollectionJob create = new NewsCollectionJob(configId, keywordId, siteId, keyword, siteName, siteUrl, cronExpression);

        try {
            batchJobExecutor.execute(create);
        } catch (Exception e) {
            throw new JobExecutionException("Failed To Execute Batch: " + e.getMessage(), e);
        }
    }
}
