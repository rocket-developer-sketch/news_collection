package com.ddi.assessment.news.batch.job;

import com.ddi.assessment.news.batch.dto.NewKeywordScheduleRequest;
import com.ddi.assessment.news.batch.dto.UpdateKeywordScheduleRequest;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobSchedulerService {
    private final static Logger log = LoggerFactory.getLogger(JobSchedulerService.class);

    private final Scheduler scheduler;
    private final BatchJobExecutor batchJobExecutor;

    public JobSchedulerService(Scheduler scheduler, BatchJobExecutor batchJobExecutor) {
        this.scheduler = scheduler;
        this.batchJobExecutor = batchJobExecutor;
    }

    public void registerJobs(List<NewKeywordScheduleRequest> requests) {
        List<JobKey> registeredJobKeys = new ArrayList<>();

        try {
            for (NewKeywordScheduleRequest request : requests) {
                JobKey jobKey = createNewsGroupJobKey(request.ruleId());
                TriggerKey triggerKey = createNewsGroupTriggerKey(request.ruleId());

                if (scheduler.checkExists(jobKey)) {
                    throw new IllegalStateException("Job already exists: " + jobKey);
                }

                JobDetail jobDetail = JobBuilder.newJob(QuartzNewsJob.class)
                        .withIdentity(jobKey)
                        .usingJobData("configId", request.ruleId())
                        .usingJobData("keywordId", request.keywordId())
                        .usingJobData("siteId", request.siteId())
                        .usingJobData("keyword", request.keyword())
                        .usingJobData("siteName", request.newsSite())
                        .usingJobData("siteUrl", request.siteUrl())
                        .usingJobData("cronExpression", request.cronExpression())
                        .build();

                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(request.cronExpression()))
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);
                registeredJobKeys.add(jobKey);

                batchJobExecutor.execute(new NewsCollectionJob(
                        request.ruleId(),
                        request.keywordId(),
                        request.siteId(),
                        request.keyword(),
                        request.newsSite(),
                        request.siteUrl(),
                        request.cronExpression()
                ));
            }

        } catch (Exception e) {
            for (JobKey jobKey : registeredJobKeys) {
                try {
                    scheduler.deleteJob(jobKey);
                    log.info("Rolled back Job: {}", jobKey);
                } catch (Exception deleteEx) {
                    log.warn("Failed to delete job during rollback: {}", jobKey, deleteEx);
                }
            }

            throw new RuntimeException("Job registration failed. All previously registered jobs rolled back.", e);
        }
    }

    public void registerJob(NewKeywordScheduleRequest request) {
        JobKey jobKey = createNewsGroupJobKey(request.ruleId());
        TriggerKey triggerKey = createNewsGroupTriggerKey(request.ruleId());

        try {
            if (scheduler.checkExists(jobKey)) {
                log.warn("Job already exists: {}", jobKey);
                return;
            }

            // Quartz 에 등록할 job (앞으로 주기적으로 실행 될 때 필요한 정보)
            JobDetail jobDetail = JobBuilder.newJob(QuartzNewsJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("configId", request.ruleId())
                    .usingJobData("keywordId", request.keywordId())
                    .usingJobData("siteId", request.siteId())
                    .usingJobData("keyword", request.keyword())
                    .usingJobData("siteName", request.newsSite())
                    .usingJobData("siteUrl", request.siteUrl())
                    .usingJobData("cronExpression", request.cronExpression())
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(request.cronExpression()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            // 최초 등록 시 즉시 한 번 실행
            batchJobExecutor.execute(new NewsCollectionJob(
                    request.ruleId(),
                    request.keywordId(),
                    request.siteId(),
                    request.keyword(),
                    request.newsSite(),
                    request.siteUrl(),
                    request.cronExpression())
            );

            log.info("Job registered and executed once: configId={}", request.ruleId());

        } catch (Exception e) {
            throw new RuntimeException("Job registration failed", e);
        }
    }

    // active 검사 후 업데이트
    // false 면 job 삭제
    // false 였다가 true 로 바꾸는 거면, job 등록(이때는 최초 처럼 즉시 실행은 아님)
    public void updateJob(UpdateKeywordScheduleRequest request) {
        JobKey jobKey = createNewsGroupJobKey(request.ruleId()); //
        TriggerKey triggerKey = createNewsGroupTriggerKey(request.ruleId());

        try {

            boolean jobExists = scheduler.checkExists(jobKey);

            if (!request.isActive()) {
                if (jobExists) {
                    // 비활성화 할 job 있으면 삭제
                    scheduler.deleteJob(jobKey);
                    log.info("Job deleted: configId={}", request.ruleId());
                }

                return;
            }

            if (!jobExists) {
                // 스케줄 등록만 하되 즉시 실행은 하지 않음
                JobDetail jobDetail = JobBuilder.newJob(QuartzNewsJob.class)
                        .withIdentity(jobKey)
                        .usingJobData("configId", request.ruleId())
                        .usingJobData("keywordId", request.keywordId())
                        .usingJobData("siteId", request.siteId())
                        .usingJobData("keyword", request.keyword())
                        .usingJobData("siteName", request.newsSite())
                        .usingJobData("siteUrl", request.siteUrl())
                        .usingJobData("cronExpression", request.cronExpression())
                        .build();

                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(request.cronExpression()))
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);

                log.info("Job re-registered (from inactive): configId={}", request.ruleId());

            } else {
                // 기존 트리거 주기 만 변경
                CronTrigger newTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(request.cronExpression()))
                        .build();

                scheduler.rescheduleJob(triggerKey, newTrigger);
                log.info("Job updated: configId={}, cron={}", request.ruleId(), request.cronExpression());
            }

        } catch (SchedulerException e) {
            throw new RuntimeException("Job update failed", e);
        }
    }

    public void deleteJob(Long ruleId) {
        JobKey jobKey = createNewsGroupJobKey(ruleId);

        try {
            scheduler.deleteJob(jobKey);
            log.info("Job deleted: configId={}", ruleId);
        } catch (SchedulerException e) {
            throw new RuntimeException("Job deletion failed", e);
        }
    }

    private JobKey createNewsGroupJobKey(Long ruleId) {
        return JobKey.jobKey("job_config_" + ruleId, "newsGroup");
    }

    private TriggerKey createNewsGroupTriggerKey(Long ruleId) {
        return TriggerKey.triggerKey("trigger_config_" + ruleId, "newsGroup");
    }

}
