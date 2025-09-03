package com.ddi.assessment.news.batch.external;

import com.ddi.assessment.news.batch.collector.utils.HttpUtils;
import com.ddi.assessment.news.batch.config.NaverNewsApiProperties;
import com.ddi.assessment.news.batch.config.TestNewsDbConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ContextConfiguration;

@DisplayName("Naver 뉴스 API 연결 테스트")
@ContextConfiguration(classes = {TestNewsDbConfig.class})
@SpringBootTest
@ActiveProfiles("test")
class NaverApiConnectionTest {

    @Autowired
    private NaverNewsApiProperties apiProperties;

    @Test
    @DisplayName("Naver 뉴스 API에 정상 연결되며 응답 본문에 'items' 포함")
    void shouldCall_NaverNewsApiSuccessfully() {
        // Given: API 호출 URL 및 헤더 준비
        String keyword = URLEncoder.encode("인공지능", StandardCharsets.UTF_8);
        String apiBase = "https://openapi.naver.com/v1/search/news.json";
        String queryParams = "?query=" + keyword + "&display=1&start=1";
        String fullUrl = apiBase + queryParams;

        Map<String, String> headers = new HashMap<>();
        headers.put(apiProperties.getHeaders().get(0), apiProperties.getClientId());
        headers.put(apiProperties.getHeaders().get(1), apiProperties.getSecret());

        String response = HttpUtils.get(fullUrl, headers);

        assertThat(response).isNotBlank();
        assertThat(response).contains("items");
    }
}
