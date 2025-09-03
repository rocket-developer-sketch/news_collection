package com.ddi.assessment.news.batch.collector.google;

import com.ddi.assessment.news.batch.collector.dto.GoogleApiItem;
import com.ddi.assessment.news.batch.collector.dto.GoogleApiResponse;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.utils.HttpUtils;
import com.ddi.assessment.news.batch.config.GoogleNewsApiProperties;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.domain.article.service.NewsArticleService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("GoogleParser")
public class GoogleNewsParser {
    Logger log = LoggerFactory.getLogger(GoogleNewsParser.class);

    private final GoogleNewsApiProperties apiProperties;
    private final NewsArticleService newsArticleService;
    private final String key;
    private final String cx;
    private final String headerReferer;

    public GoogleNewsParser(GoogleNewsApiProperties apiProperties, NewsArticleService newsArticleService) {
        this.apiProperties = apiProperties;
        this.newsArticleService = newsArticleService;
        this.key = apiProperties.getKey();
        this.cx = apiProperties.getCx();
        this.headerReferer = apiProperties.getHeaderReferer();
    }

    public List<ParsedNewsArticle> extractArticles(NewsCollectionJob job) throws URISyntaxException {
        String keyword = job.getKeyword();
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String siteName = job.getNewsSite();

        List<GoogleApiResponse> articles = new ArrayList<>();
        String urlTemplate = job.getSiteUrl();
        int perDisplay = 3; // 한 번에 3개
        int maxDisplay = 6; // 총 6 개만
        int totalRequests = (int) Math.ceil((double) maxDisplay / perDisplay);
        for (int i = 0; i < totalRequests; i++) {
            int start = (i * perDisplay) + 1;

            String query = urlTemplate
                    .replace("{apikey}", key)
                    .replace("{cx}", cx)
                    .replace("{query}", encodedKeyword)
                    .replace("{start}", String.valueOf(start * perDisplay + 1))
                    .replace("{num}", String.valueOf(perDisplay));

            URI uri = new URI(query);

            // 구글 검색 API 요청
            String response = null;
            for (int retry = 0; retry < 3; retry++) {
                try {
                    response = HttpUtils.get(uri.toString(), Map.of("Referer", headerReferer));
                    break;
                } catch (IllegalArgumentException e) {
                    log.warn("Failed To API Request ({} times): {}", retry + 1, e.getMessage());
                } catch (Exception e) {
                    log.error("Unexpected Error Occurred ({} times): {}", retry + 1, e.getMessage(), e);
                }
            }

            if (response == null) throw new RuntimeException("Failed To Get Google Search API Response");


            GoogleApiResponse parsedResponse = parseResponse(response);
            articles.add(parsedResponse);
        }

        return toParsedNewsArticle(keyword, siteName, articles);
    }

    private List<ParsedNewsArticle> toParsedNewsArticle(String keyword, String siteName, List<GoogleApiResponse> articles) {
        return articles.stream()
                    .flatMap(response -> response.getItems()
                            .stream()
                            .map(article -> new ParsedNewsArticle(
                                    null,
                                    keyword,
                                    siteName,
                                    article.getLink(),
                                    article.getTitle(),
                                    article.getSnippet(),
                                    LocalDateTime.now()
                            ))

                    ).collect(Collectors.toList());
    }

    private GoogleApiResponse parseResponse(String responseJson) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);
            JsonNode items = root.path("items");

            log.debug("Google API Response Item Count: {}", items.size());

            List<GoogleApiItem> results = new ArrayList<>();
            for (JsonNode item : items) {
                results.add(new GoogleApiItem(
                        item.path("title").asText(),
                        item.path("link").asText(),
                        // snippet 이 존재하지 않을 수 있습니다.
                        item.path("snippet").asText()
                ));
            }

            return new GoogleApiResponse(results);

        } catch (Exception e) {
            throw new RuntimeException("Failed Parsing Google Search API Response.", e);
        }
    }
}
