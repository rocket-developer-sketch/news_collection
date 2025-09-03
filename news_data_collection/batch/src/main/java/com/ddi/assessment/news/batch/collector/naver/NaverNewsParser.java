package com.ddi.assessment.news.batch.collector.naver;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import com.ddi.assessment.news.batch.collector.utils.HttpUtils;
import com.ddi.assessment.news.batch.collector.dto.NaverApiResponse;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.config.NaverNewsApiProperties;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("NaverParser")
public class NaverNewsParser {
    Logger log = LoggerFactory.getLogger(NaverNewsParser.class);

    private final NaverNewsApiProperties apiProperties;
    private final NewsArticleService newsArticleService;
    private final static DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
    private final String clientId;
    private final String clientSecret;
    private final String clientHeader;
    private final String secretHeader;

    public NaverNewsParser(NaverNewsApiProperties apiProperties, NewsArticleService newsArticleService) {
        this.apiProperties = apiProperties;
        this.clientId = apiProperties.getClientId(); //애플리케이션 클라이언트 아이디
        this.clientSecret = apiProperties.getSecret();
        this.clientHeader = apiProperties.getHeaders().get(0);
        this.secretHeader = apiProperties.getHeaders().get(1);
        this.newsArticleService = newsArticleService;
    }

    public List<ParsedNewsArticle> extractArticles(NewsCollectionJob job) throws URISyntaxException {

        String keyword = job.getKeyword();
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String siteName = job.getNewsSite();

        Map<String, String> headers = new HashMap<>();
        headers.put(clientHeader, clientId);
        headers.put(secretHeader, clientSecret);

        List<NaverApiResponse> articles = new ArrayList<>();
        String urlTemplate = job.getSiteUrl();
        int display = 100;
        for (int start = 1; start <= 1000; start += display) {

            String query = urlTemplate
                    .replace("{query}", encodedKeyword)
                    .replace("{display}", String.valueOf(display))
                    .replace("{start}", String.valueOf(start));

            URI uri = new URI(query);

            // 네이버 검색 API 요청
            String response = null;
            for (int retry = 0; retry < 3; retry++) {
                try {
                    response = HttpUtils.get(uri.toString(), headers);
                    break;
                } catch (IllegalArgumentException e) {
                    log.warn("Failed To API Request ({} times): {}", retry + 1, e.getMessage());
                } catch (Exception e) {
                    log.error("Unexpected Error Occurred ({} times): {}", retry + 1, e.getMessage(), e);
                }
            }

            if (response == null) throw new RuntimeException("Failed To Get Naver Search API Response");


            List<NaverApiResponse> parsedResponse = parseResponse(response);
            articles.addAll(parsedResponse);

            if (parsedResponse.size() < display) break;
        }

        return toParsedNewsArticle(keyword, siteName, articles);
    }

    private List<ParsedNewsArticle> toParsedNewsArticle(String keyword, String siteName, List<NaverApiResponse> articles) {
        return articles.stream()
                .map(article -> new ParsedNewsArticle(
                        null,
                        keyword,
                        siteName,
                        article.getLink(),
                        article.getTitle(),
                        article.getDescription(),
                        article.getPubDate()
                ))
                .collect(Collectors.toList());
    }

    private List<NaverApiResponse> parseResponse(String responseJson) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);
            JsonNode items = root.path("items");

            List<NaverApiResponse> results = new ArrayList<>();
            for (JsonNode item : items) {
                results.add(new NaverApiResponse(
                        cleanHtml(item.path("title").asText()),
                        item.path("link").asText(),
                        item.path("originallink").asText(),
                        cleanHtml(item.path("description").asText()),
                        ZonedDateTime.parse(item.path("pubDate").asText(), formatter).toLocalDateTime())
                );
            }

            return results;

        } catch (Exception e) {
            throw new RuntimeException("Failed Parsing Naver Search API Response", e);
        }
    }

    private static String cleanHtml(String htmlText) {
        return htmlText.replaceAll("<[^>]*>", ""); // <b>, <br> 등 제거
    }
}
