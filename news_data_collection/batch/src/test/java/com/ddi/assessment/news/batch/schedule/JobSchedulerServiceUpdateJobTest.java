package com.ddi.assessment.news.batch.schedule;

import com.ddi.assessment.news.batch.dto.UpdateKeywordScheduleRequest;
import com.ddi.assessment.news.batch.job.BatchJobExecutor;
import com.ddi.assessment.news.batch.job.JobSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.quartz.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("JobSchedulerService.updateJob 테스트")
@ExtendWith(MockitoExtension.class)
public class JobSchedulerServiceUpdateJobTest {

    @Mock
    Scheduler scheduler;

    @Mock
    BatchJobExecutor batchJobExecutor;

    @InjectMocks
    JobSchedulerService jobSchedulerService;

    UpdateKeywordScheduleRequest activeRequest;
    UpdateKeywordScheduleRequest inactiveRequest;

    @BeforeEach
    void setUp() {
        activeRequest = new UpdateKeywordScheduleRequest(
                1L, 2L, 1L, "AI", "NAVER", "http://site.url", "0/30 * * * * ?", true
        );

        inactiveRequest = new UpdateKeywordScheduleRequest(
                1L, 2L, 1L, "AI", "NAVER", "http://site.url", "0/30 * * * * ?", false
        );
    }

    @Nested
    @DisplayName("isActive=false 인 경우")
    class WhenInactive {

        @Test
        @DisplayName("이미 job이 존재하면 삭제")
        void deletesJob_IfExists() throws Exception {

            when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

            jobSchedulerService.updateJob(inactiveRequest);

            verify(scheduler).deleteJob(any(JobKey.class));
            verify(scheduler, never()).scheduleJob(any(), any());
            verify(scheduler, never()).rescheduleJob(any(), any());
        }

        @Test
        @DisplayName("job 없으면 아무 동작 없음")
        void doesNothing_IfJobNotExists() throws Exception {
            when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

            jobSchedulerService.updateJob(inactiveRequest);

            verify(scheduler, never()).deleteJob(any());
            verify(scheduler, never()).scheduleJob(any(), any());
            verify(scheduler, never()).rescheduleJob(any(), any());
        }
    }

    @Nested
    @DisplayName("isActive=true 인 경우")
    class WhenActive {

        @Test
        @DisplayName("job 없으면 새로 등록 (즉시 실행 없음)")
        void registersNewJob_IfNotExists() throws Exception {
            when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

            jobSchedulerService.updateJob(activeRequest);

            verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
            verify(scheduler, never()).rescheduleJob(any(), any());
            verify(batchJobExecutor, never()).execute(any());
        }

        @Test
        @DisplayName("job 있으면 주기만 갱신")
        void updates_ExistingJobSchedule() throws Exception {
            when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

            jobSchedulerService.updateJob(activeRequest);

            verify(scheduler).rescheduleJob(any(TriggerKey.class), any(CronTrigger.class));
            verify(scheduler, never()).scheduleJob(any(), any());
        }
    }

    @Test
    @DisplayName("scheduler 예외 발생 시 RuntimeException 발생")
    void throwsRuntimeException_OnSchedulerFailure() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenThrow(new SchedulerException("scheduler 테스트 중단"));

        assertThatThrownBy(() -> jobSchedulerService.updateJob(activeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Job update failed") // 메시지 바꾸면 여기도 수정
                .hasCauseInstanceOf(SchedulerException.class);
    }
}
