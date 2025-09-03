package com.ddi.assessment.news.batch.collector.daum;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsPreview;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DaumNewsParser {
    Logger log = LoggerFactory.getLogger(DaumNewsParser.class);

    public List<ParsedNewsPreview> extractPreviews(Long siteId, String listUrl) {
        List<ParsedNewsPreview> result = new ArrayList<>();

        try {
            Document doc = getJsoupDoc(listUrl);

            log.info("Successfully fetched the document from: {}", listUrl);

            Elements items = doc.select("ul.c-list-basic > li");

            for (Element item : items) {
                // 상세 뉴스 url 찾기
                Element linkElement = item.selectFirst(".item-title a");
                if (linkElement == null) continue;

                String url = linkElement.absUrl("href");

                // 상세 뉴스에서 뉴스 ID 추출
                Long newsId = extractNewsId(url);

                Elements infoSpans = item.select(".item-contents .txt_info");
                String dateText = extractDateText(infoSpans);
                LocalDate publishedDate = parseToLocalDate(dateText);

                result.add(new ParsedNewsPreview(url, newsId, publishedDate));
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed To Crawling Daum News List Page: " + listUrl, e);
        }

        return result;
    }

    public List<ParsedNewsArticle> extractArticles(NewsCollectionJob job) {
        List<ParsedNewsArticle> articles = new ArrayList<>();

        for (ParsedNewsPreview preview : job.getParsedArticlesHolder().getPreviews()) {
            ParsedNewsArticle article = extractArticle(job, preview.getUrl());

            articles.add(article);
        }

        return articles;
    }

    public ParsedNewsArticle extractArticle(NewsCollectionJob job, String detailUrl) {
        try {
            Document doc = getJsoupDoc(detailUrl);
            Long newsId = extractNewsId(detailUrl);

            String title = doc.selectFirst("h3.tit_view").text();

            String date = doc.selectFirst("span.num_date").text();
            LocalDateTime publishedAt = parsePublishedDate(date);

            Elements news = doc.select("div.article_view section p");

            String content = news.stream()
                    .filter(p -> p.select("img").isEmpty()) // 이미지 없는 문단만
                    .map(Element::text)
                    .filter(text -> !text.isBlank())
                    .collect(Collectors.joining("\n\n"));

            return new ParsedNewsArticle (
                    newsId,
                    job.getKeyword(),
                    job.getNewsSite(),
                    detailUrl,
                    title,
                    content,
                    publishedAt
            );


        } catch (IOException e) {
            throw new RuntimeException("Failed To Crawling Daum News Content Page: " + detailUrl, e);
        }
    }

    private Document getJsoupDoc(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/114.0.0.0 Safari/537.36")
                .referrer("https://www.google.com")
                .timeout(5000)
                .maxBodySize(1024 * 1024)
                .get();
    }


    private String extractDateText(Elements infoSpans) {
        for (Element span : infoSpans) {
            String text = span.text().trim();

            // yyyy.MM.dd 형식 또는 "~전"
            if (text.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) return text;
            if (text.matches(".*(시간|분)전")) return text;
        }

        return null;
    }

    private LocalDate parseToLocalDate(String text) {
        if (text == null || text.isBlank()) {
            log.error("Date text is null or empty: {}", text);
            return null;
        }

        if (text.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            return LocalDate.parse(text, formatter);
        }

        // 오늘 올라 온 기사
        if (text.contains("시간전") || text.contains("분전")) {
            return LocalDate.now();
        }

        return null;
    }

    private Long extractNewsId(String url) {
        Pattern pattern = Pattern.compile("/v/(\\d{17})");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) return Long.valueOf(matcher.group(1));

        return null;
    }

    private LocalDateTime parsePublishedDate(String rawText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d. H:mm", Locale.KOREA);
        return LocalDateTime.parse(rawText.trim(), formatter);
    }
}
