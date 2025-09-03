package com.ddi.assessment.news.batch.schedule;

import com.ddi.assessment.news.batch.job.BatchJobExecutor;
import com.ddi.assessment.news.batch.job.JobSchedulerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("JobSchedulerService.deleteJob 테스트")
@ExtendWith(MockitoExtension.class)
public class JobSchedulerServiceDeleteJobTest {

    @Mock
    Scheduler scheduler;

    @Mock
    BatchJobExecutor batchJobExecutor;

    @InjectMocks
    JobSchedulerService jobSchedulerService;

    @Test
    @DisplayName("존재하는 job 정상 삭제 완료")
    void deletesJob_Successfully() throws Exception {
        Long configId = 1L;

        jobSchedulerService.deleteJob(configId);

        verify(scheduler).deleteJob(JobKey.jobKey("job_config_1", "newsGroup"));
    }

    @Test
    @DisplayName("삭제 중 예외가 발생하면 RuntimeException 발생")
    void throwsRuntimeException_OnDeleteFailure() throws Exception {

        Long configId = 1L;
        doThrow(new SchedulerException("테스트 scheduler 삭제 실패")).when(scheduler)
                .deleteJob(JobKey.jobKey("job_config_1", "newsGroup"));

        assertThatThrownBy(() -> jobSchedulerService.deleteJob(configId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Job deletion failed") // 메시지 바꾸면 여기도 수정
                .hasCauseInstanceOf(SchedulerException.class);
    }
}
