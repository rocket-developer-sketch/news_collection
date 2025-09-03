package com.ddi.assessment.news.batch.collector.daum;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsPreview;
import com.ddi.assessment.news.batch.collector.NewsCollectorService;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("DAUM")
public class DaumCollectorServiceImpl implements NewsCollectorService {

    private final DaumNewsParser parser;

    public DaumCollectorServiceImpl(DaumNewsParser parser) {
        this.parser = parser;
    }

    public List<ParsedNewsPreview> collectPreviews(NewsCollectionJob job) {
        // nil_search=end 로 하니 페이징 까지 동작 함
        // https://search.daum.net/search?w=news&nil_search=end&q=비트코인&p=2

        String keyword = job.getKeyword();
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String url = job.getSiteUrl().split("&p=")[0];
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<ParsedNewsPreview> allPreviews = new ArrayList<>();
        int page = 1;
        boolean stop = false;

        while (!stop) {
            String pagedUrl = url.replace("{keyword}", encodedKeyword) + "&p=" + page;

            List<ParsedNewsPreview> previews = parser.extractPreviews(job.getSiteId(), pagedUrl);
            if (previews.isEmpty()) break;

            for (ParsedNewsPreview preview : previews) {
                if (preview.getPublishedDate() !=null && preview.getPublishedDate().isBefore(yesterday)) {
                    stop = true;
                    break;
                }
                allPreviews.add(preview);
            }

            page++;
        }

        return allPreviews;
    }

    public List<ParsedNewsArticle> collectArticles(NewsCollectionJob job) {
        return parser.extractArticles(job);
    }



}
