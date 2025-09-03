package com.ddi.assessment.news.api.scheduler;


import com.ddi.assessment.news.api.scheduler.applicaiton.ScheduleApiService;
import com.ddi.assessment.news.api.scheduler.client.ScheduleApiClient;
import com.ddi.assessment.news.api.scheduler.dto.NewScheduleRequest;

import com.ddi.assessment.news.domain.collectrule.repository.CollectionRuleRepository;
import com.ddi.assessment.news.domain.interval.entity.JpaInterval;
import com.ddi.assessment.news.domain.interval.repository.JpaIntervalRepository;
import com.ddi.assessment.news.domain.keyword.repository.JpaKeywordRepository;
import com.ddi.assessment.news.domain.site.entity.JpaNewsSite;
import com.ddi.assessment.news.domain.site.repository.JpaNewsSiteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test") // application-test.yml 사용
public class ScheduleApiServiceImplIntegrationTest {

    @Autowired
    private ScheduleApiService scheduleApiService;

    @Autowired
    private CollectionRuleRepository collectionRuleRepository;

    @Autowired
    private JpaKeywordRepository jpaKeywordRepository;

    @Autowired
    private JpaNewsSiteRepository jpaNewsSiteRepository;

    @Autowired
    private JpaIntervalRepository jpaIntervalRepository;

    @MockBean
    private ScheduleApiClient scheduleApiClient;

    @Test
    @DisplayName("예외 발생 시 트랜잭션 전체 롤백 - 등록 취소됨")
    void testRegisterScheduleRollback() {
        jpaNewsSiteRepository.save(new JpaNewsSite("NAVER", "https://naver.com?q={keyword}"));
        jpaIntervalRepository.save(new JpaInterval("15분마다", "0 0/15 * * * ?"));

        NewScheduleRequest request = new NewScheduleRequest(
                List.of("반도체", "AI"),
                List.of("NAVER"),
                "15분마다"
        );

        doThrow(new RuntimeException("스케줄 등록 실패"))
                .when(scheduleApiClient).registerJobs(any());

        assertThrows(RuntimeException.class, () -> {
            scheduleApiService.registerSchedules(1L, request);
        });

        assertTrue(jpaKeywordRepository.findByWord("반도체").isEmpty(), "반도체 키워드 롤백되어야 함");
        assertTrue(jpaKeywordRepository.findByWord("AI").isEmpty(), "AI 키워드 롤백되어야 함");

        assertTrue(collectionRuleRepository.findAll().isEmpty(), "수집 규칙 롤백되어야 함");
    }
}
