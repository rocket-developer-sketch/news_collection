package com.ddi.assessment.news.batch.schedule;

import com.ddi.assessment.news.batch.dto.NewKeywordScheduleRequest;
import com.ddi.assessment.news.batch.job.BatchJobExecutor;
import com.ddi.assessment.news.batch.job.JobSchedulerService;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("JobSchedulerService.registerJob(s) 테스트")
@ExtendWith(MockitoExtension.class)
class JobSchedulerServiceJobExistsTest {

    @Mock
    Scheduler scheduler;

    @Mock
    BatchJobExecutor batchJobExecutor;

    @InjectMocks
    JobSchedulerService jobSchedulerService;

    @Test
    @DisplayName("새로 등록 할 job 이미 존재하면 스케줄 등록 및 즉시 실행 하지 않음")
    void shouldNotRegisterOrExecuteJob_whenJobAlreadyExists() throws Exception {
        NewKeywordScheduleRequest request = new NewKeywordScheduleRequest(
                1L, 2L, 1L, "인공지능", "NAVER", "http://site.url.example.com", "0 */15 * * * ?"
        );

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        jobSchedulerService.registerJob(request);

        verify(scheduler, never()).scheduleJob(any(), any());
        verify(batchJobExecutor, never()).execute(any());
    }

    @Test
    @DisplayName("새로 등록하는 job 정상적으로 모든 스케줄이 등록되고, 배치 즉시 실행 호출됨")
    void testRegisterJobs_successfulRegistration() throws Exception {
        NewKeywordScheduleRequest request = new NewKeywordScheduleRequest(
                100L, 1L, 2L, "인공지능", "NAVER", "http://site.url.example.com", "0 0/15 * * * ?"
        );

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        jobSchedulerService.registerJobs(List.of(request));

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        verify(batchJobExecutor).execute(any(NewsCollectionJob.class));
    }

    @Test
    @DisplayName("이미 존재하는 jobkey 가 있으면 예외 발생 후 중단")
    void testRegisterJobs_duplicateJobKey() throws Exception {
        NewKeywordScheduleRequest request = new NewKeywordScheduleRequest(
                1L, 2L, 1L, "삼성전자", "DAUM", "http://daum.example.com", "0 */15 * * * ?"
        );

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> jobSchedulerService.registerJobs(List.of(request))
        );

        verify(scheduler, never()).scheduleJob(any(), any());
        verify(batchJobExecutor, never()).execute(any());
    }

    @Test
    @DisplayName("job 등록 도중 예외 발생 시, 등록된 job 모두 rollback")
    void testRegisterJobs_partialFailureWithRollback() throws Exception {
        NewKeywordScheduleRequest req1 = new NewKeywordScheduleRequest(
                1L, 2L, 1L, "삼성전자", "DAUM", "http://daum.example.com", "0 */15 * * * ?"
        );
        NewKeywordScheduleRequest req2 = new NewKeywordScheduleRequest(
                2L, 3L, 2L, "주식", "NAVER", "http://naver.example.com", "0 0 * * * ?"
        );

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        doAnswer(invocation -> null) // 1회차 성공 처리
                .doThrow(new RuntimeException("등록 실패")) // 2회차 부터는 실패
                .when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        assertThrows(RuntimeException.class, () -> jobSchedulerService.registerJobs(List.of(req1, req2)));

        verify(scheduler, times(1)).deleteJob(any(JobKey.class)); // rollback 확인
    }

    @Test
    @DisplayName("배치 실행 도중 예외 발생하면 등록된 job 모두 rollback")
    void testRegisterJobs_batchExecutionFails_rollback() throws Exception {
        NewKeywordScheduleRequest req = new NewKeywordScheduleRequest(
                1L, 2L, 1L, "경복궁", "DAUM", "http://daum.example.com", "0 */15 * * * ?"
        );

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        verify(scheduler, never()).scheduleJob(any(), any());
        doThrow(new RuntimeException("배치 실패"))
                .when(batchJobExecutor)
                .execute(any());

        assertThrows(RuntimeException.class, () -> jobSchedulerService.registerJobs(List.of(req)));

        verify(scheduler, times(1)).deleteJob(any(JobKey.class));
    }

}
