package com.ddi.assessment.news.batch.job;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("뉴스 사이트별 전략에 따라 Job이 다르게 구성되는지 테스트")
class NewsJobFactoryTest {

    @Test
    @DisplayName("NAVER 플랫폼에 대해 Naver Job Strategy 호출된다")
    void shouldUseNaverStrategy_whenSiteIsNAVER() {
        NewsJobStrategy naverStrategy = mock(NewsJobStrategy.class);
        when(naverStrategy.getNewsSiteName()).thenReturn("NAVER");

        NewsCollectionJob input = new NewsCollectionJob(1L, 2L, 3L, "연말정산", "NAVER", "https://www.naver.com", "*/10 * * * *");
        Job mockJob = mock(Job.class);
        when(naverStrategy.buildJob(input)).thenReturn(mockJob);

        NewsJobFactory factory = new NewsJobFactory(List.of(naverStrategy));
        Job result = factory.build(input);

        assertThat(result).isEqualTo(mockJob);
        verify(naverStrategy).buildJob(input);
    }

    @Test
    @DisplayName("DAUM 플랫폼에 대해 Daum job strategy 호출")
    void shouldUseDaumStrategy_whenSiteIsDAUM() {
        NewsJobStrategy daumStrategy = mock(NewsJobStrategy.class);
        when(daumStrategy.getNewsSiteName()).thenReturn("DAUM");

        NewsCollectionJob input = new NewsCollectionJob(4L, 5L, 6L, "서울맛집", "DAUM", "https://www.daum.net", "*/5 * * * *");
        Job mockJob = mock(Job.class);
        when(daumStrategy.buildJob(input)).thenReturn(mockJob);

        NewsJobFactory factory = new NewsJobFactory(List.of(daumStrategy));
        Job result = factory.build(input);

        assertThat(result).isEqualTo(mockJob);
        verify(daumStrategy).buildJob(input);
    }

    @Test
    @DisplayName("플랫폼 이름이 소문자여도 대문자로 job strategy 선택")
    void shouldMatchStrategy_caseInsensitive() {
        NewsJobStrategy daumStrategy = mock(NewsJobStrategy.class);
        when(daumStrategy.getNewsSiteName()).thenReturn("DAUM");

        NewsCollectionJob input = new NewsCollectionJob(7L, 8L, 9L, "미국경제", "daum", "https://www.daum.net", "*/15 * * * *");
        Job mockJob = mock(Job.class);
        when(daumStrategy.buildJob(input)).thenReturn(mockJob);

        NewsJobFactory factory = new NewsJobFactory(List.of(daumStrategy));
        Job result = factory.build(input);

        assertThat(result).isEqualTo(mockJob);
        verify(daumStrategy).buildJob(input);
    }

    @Test
    @DisplayName("지원하지 않는 플랫폼일 경우 예외가 발생")
    void shouldThrowException_whenUnknownPlatform() {
        NewsJobFactory factory = new NewsJobFactory(List.of()); // 전략 없음

        NewsCollectionJob input = new NewsCollectionJob(10L, 11L, 12L, "날씨예보", "GOOGLE", "https://www.google.com", "*/20 * * * *");

        assertThatThrownBy(() -> factory.build(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported News Site");
    }
}
