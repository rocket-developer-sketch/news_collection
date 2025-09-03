package com.ddi.assessment.news.api.scheduler;

import com.ddi.assessment.news.api.scheduler.applicaiton.ScheduleApiServiceImpl;
import com.ddi.assessment.news.api.scheduler.client.ScheduleApiClient;
import com.ddi.assessment.news.api.scheduler.dto.NewScheduleRequest;
import com.ddi.assessment.news.api.scheduler.dto.UpdateScheduleRequest;
import com.ddi.assessment.news.api.scheduler.dto.UpdateScheduleApiRequest;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import com.ddi.assessment.news.domain.collectrule.service.CollectionRuleService;
import com.ddi.assessment.news.domain.collectrule.vo.ExistingCollectionRule;
import com.ddi.assessment.news.domain.interval.service.IntervalService;
import com.ddi.assessment.news.domain.interval.vo.IntervalCronExp;
import com.ddi.assessment.news.domain.keyword.service.KeywordService;
import com.ddi.assessment.news.domain.keyword.vo.ExistingKeyword;
import com.ddi.assessment.news.domain.keyword.vo.NewKeywordCommand;
import com.ddi.assessment.news.domain.site.service.NewsSiteService;
import com.ddi.assessment.news.domain.site.vo.ExistingNewsSite;
import com.ddi.assessment.news.domain.site.vo.NewsSiteQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@DisplayName("사용자 스케줄 설정 흐름 테스트")
@ExtendWith(MockitoExtension.class)
class ScheduleApiServiceImplTest {
    @Mock
    ScheduleApiClient scheduleApiClient;

    @Mock
    CollectionRuleService collectionRuleService;

    @Mock
    KeywordService keywordService;

    @Mock
    IntervalService intervalService;

    @Mock
    NewsSiteService newsSiteService;

    @Mock
    NewsArticleService newsArticleService;

    @InjectMocks
    ScheduleApiServiceImpl scheduleApiService;

    private NewScheduleRequest newRequest;
    private UpdateScheduleRequest updateRequest;

    @BeforeEach
    void setUp() {
        newRequest = new NewScheduleRequest(
                List.of("불닭볶음면", "김수출"),
                List.of("NAVER"),
                "10분마다"
        );

        updateRequest = new UpdateScheduleRequest(1L, "불닭볶음면", "NAVER", "30분마다", true);
    }

    @Test
    @DisplayName("registerSchedule - 새로운 사용자 스케줄에 대해 db 에 저장 후, 배치 client 에 결과 전달")
    void testRegisterSchedule() {
        NewScheduleRequest request = new NewScheduleRequest(
                List.of("불닭볶음면", "신라면"),
                List.of("NAVER"),
                "15분마다"
        );

        when(intervalService.getCronExp(any()))
                .thenReturn(new IntervalCronExp(1L, "0 0/10 * * * ?"));

        when(keywordService.findOrCreateKeywords(any()))
                .thenReturn(Set.of(
                        new ExistingKeyword(1L, "불닭볶음면"),
                        new ExistingKeyword(2L, "신라면")
                ));

        when(newsSiteService.getSites(any()))
                .thenReturn(Set.of(
                        new ExistingNewsSite(2L, "NAVER", "http://naver.com?q={keyword}")
                ));
        
        // 중복 없음
        when(collectionRuleService.getUserRulesByKeywordAndSiteAndInterval(
                any(), anySet(), anySet(), anyString()))
                .thenReturn(Set.of());

        when(collectionRuleService.createJobs(any()))
                .thenReturn(List.of(
                        new ExistingCollectionRule(1L, 3L, 1L, 2L, 1L, true),
                        new ExistingCollectionRule(1L, 4L, 2L, 2L, 1L, true)
                ));

        scheduleApiService.registerSchedules(1L, request);

        verify(scheduleApiClient).registerJobs(argThat(requests -> {
            return requests.size() == 2;
        }));
    }

    @Test
    @DisplayName("updateSchedule - isActive=true 일 때 배치 업데이트 client 호출")
    void testUpdateSchedule_active() {
        when(intervalService.getCronExp(any())).thenReturn(new IntervalCronExp(1L, "0 0/10 * * * ?"));

        when(collectionRuleService.updateJob(any())).thenReturn(1); // 업데이트 성공 갯수

        when(keywordService.findOrCreateKeyword(any(NewKeywordCommand.class))).thenReturn(new ExistingKeyword(1L, "불닭볶음면"));
        when(newsSiteService.getSite(any(NewsSiteQuery.class))).thenReturn(new ExistingNewsSite(2L, "NAVER", "http://naver.com?q={keyword}"));

        scheduleApiService.updateSchedule(updateRequest);

        verify(scheduleApiClient).update(any(UpdateScheduleApiRequest.class));
    }

    @Test
    @DisplayName("updateSchedule - isActive=false 일 때 배치 삭제 client 호출")
    void testUpdateSchedule_inactive() {
        UpdateScheduleRequest inactiveRequest = new UpdateScheduleRequest(1L, "불닭볶음면", "NAVER", "30분마다", false);

        when(intervalService.getCronExp(any())).thenReturn(new IntervalCronExp(1L, "0 0/30 * * * ?"));

        when(collectionRuleService.updateJob(any())).thenReturn(1);

        scheduleApiService.updateSchedule(inactiveRequest);

        verify(scheduleApiClient).delete(1L);
    }

    @Test
    @DisplayName("deleteSchedule - db 에서 사용자 지정 스케줄 제거 후 배치 삭제 client 호출")
    void testDeleteSchedule() {
        scheduleApiService.deleteSchedule(10L);

        verify(collectionRuleService).deleteJob(10L);

        verify(scheduleApiClient).delete(10L);
    }
}
