package com.ddi.assessment.news.batch.external;

import com.ddi.assessment.news.batch.collector.naver.NaverNewsParser;
import com.ddi.assessment.news.batch.collector.utils.HttpUtils;
import com.ddi.assessment.news.batch.config.NaverNewsApiProperties;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NaverNewsParserRetryTest {
    @Mock
    NaverNewsApiProperties apiProperties;

    @Mock
    NewsArticleService articleService;

    @Test
    @DisplayName("3회 연속 HttpUtils.get 실패 시 예외를 던진다")
    void shouldThrowExceptionAfterThreeFailures() throws Exception {
        // given
        String urlTemplate = "https://openapi.naver.com/v1/search/news.json?query={query}&display={display}&start={start}";
        NewsCollectionJob job = new NewsCollectionJob(
                1L, 2L, 3L,
                "인공지능", "NAVER", urlTemplate,
                "*/10 * * * *"
        );

        when(apiProperties.getClientId()).thenReturn("testDummyId");
        when(apiProperties.getSecret()).thenReturn("testDummySecret");
        when(apiProperties.getHeaders()).thenReturn(List.of("X-Naver-Client-Id", "X-Naver-Client-Secret"));

        NaverNewsParser parser = new NaverNewsParser(apiProperties, articleService);

        try (MockedStatic<HttpUtils> mockedHttp = mockStatic(HttpUtils.class)) {
            mockedHttp.when(() -> HttpUtils.get(anyString(), anyMap()))
                    .thenThrow(new IllegalArgumentException("API Failed"));

            assertThatThrownBy(() -> parser.extractArticles(job))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed To Get Naver Search API Response"); // 문장 변경 되면 수정해야 함

            // 재시도 호출 3번 되었나 확인
            mockedHttp.verify(() -> HttpUtils.get(anyString(), anyMap()), times(3));
        }
    }
}
