package com.ddi.assessment.news.batch.external;


import com.ddi.assessment.news.batch.collector.utils.HttpUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Daum 뉴스 검색 사이트 연결 테스트")
class DaumSearchConnectionTest {

    @Test
    @DisplayName("뉴스 검색 페이지에 정상적으로 접근하면 HTML 본문 반환")
    void shouldConnectSuccessfully_ToDaumNewsSearchPage() {
        String url = "https://search.daum.net/search?w=news&q=비트코인&p=1";

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // 로봇 회피용 헤더

        String response = HttpUtils.get(url, headers);

        assertThat(response).isNotBlank();
        assertThat(response).contains("<html");
    }
}