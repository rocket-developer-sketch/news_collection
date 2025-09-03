package com.ddi.assessment.news.batch.schedule;

import com.ddi.assessment.news.batch.config.BatchSchemaInitializer;
import com.ddi.assessment.news.batch.dto.NewKeywordScheduleRequest;
import com.ddi.assessment.news.batch.job.JobSchedulerService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QuartzScheduler 통합 테스트")
@SpringBootTest
@ActiveProfiles("test")
@Import(BatchSchemaInitializer.class)
public class QuartzSchedulerIntegrationTest {

    @Autowired
    private JobSchedulerService jobSchedulerService;

    @Autowired
    private JobExplorer jobExplorer;

    @Test
    @DisplayName("최초 job 등록 시 Quartz가 즉시 실행되어 Spring Batch Job 실행")
    void shouldTriggerBatchJobImmediately_whenJobIsRegistered() throws Exception {
        // given: 등록 요청 데이터 준비
        NewKeywordScheduleRequest request = new NewKeywordScheduleRequest(
                1L, 2L, 1L,
                "인공지능",
                "DAUM",
                "https://openapi.naver.com/v1/search/news.json?query={query}&display={display}&start={start}&sort=sim",
                "0 */15 * * * ?"
        );

        jobSchedulerService.registerJob(request);

        sleep(10000);

        long executionCount = countTotalJobExecutions();
        assertThat(executionCount).isGreaterThan(0);
    }

    // 실행된 모든 JobExecution 수
    private long countTotalJobExecutions() {
        return jobExplorer.getJobNames().stream()
                .flatMap(name -> jobExplorer.getJobInstances(name, 0, 10).stream())
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .count();
    }
}
