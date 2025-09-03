package com.ddi.assessment.news.batch.config;

import com.ddi.assessment.news.batch.job.QuartzNewsJob;
import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import com.ddi.assessment.news.domain.collectrule.service.CollectionRuleService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobSchedulerInitializer implements ApplicationListener<ApplicationReadyEvent> {
    Logger log = LoggerFactory.getLogger(JobSchedulerInitializer.class);

    private final CollectionRuleService collectionRuleService;
    private final Scheduler scheduler;

    public JobSchedulerInitializer(CollectionRuleService collectionRuleService, Scheduler scheduler) {
        this.collectionRuleService = collectionRuleService;
        this.scheduler = scheduler;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<JpaCollectRule> jobs = collectionRuleService.getActiveCollectConfig();
        log.info("re-register Job size:" + jobs.size());

        for (JpaCollectRule job : jobs) {
            try {
                JobKey jobKey = JobKey.jobKey("job_config_" + job.getId(), "newsGroup");
                TriggerKey triggerKey = TriggerKey.triggerKey("trigger_config_" + job.getId(), "newsGroup");

                if (scheduler.checkExists(jobKey)) {
                    log.debug("already registered Job: jobId=" + job.getId());
                    continue;
                }

                // JobDetail 생성
                JobDetail jobDetail = JobBuilder.newJob(QuartzNewsJob.class)
                        .withIdentity(jobKey)
                        .usingJobData("configId", job.getId())
                        .usingJobData("keywordId", job.getKeyword().getId())
                        .usingJobData("siteId", job.getNewsSite().getId())
                        .usingJobData("keyword", job.getKeyword().getWord())
                        .usingJobData("siteName", job.getNewsSite().getSiteName())
                        .usingJobData("siteUrl", job.getNewsSite().getUrlTemplate())
                        .usingJobData("cronExpression", job.getInterval().getCronExpression())
                        .build();

                // Trigger 생성
                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(job.getInterval().getCronExpression()))
                        .build();

                // Job 등록
                scheduler.scheduleJob(jobDetail, trigger);
                log.debug("Job re-register completed: jobId=" + job.getId());

            } catch (SchedulerException e) {
                throw new RuntimeException("Job re-register failed: jobId=" + job.getId(), e);
            }
        }
    }
}

