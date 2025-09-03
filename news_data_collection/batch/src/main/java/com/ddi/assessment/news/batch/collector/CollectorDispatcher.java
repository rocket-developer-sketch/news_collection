package com.ddi.assessment.news.batch.collector;


import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsPreview;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

// 플랫폼 별로 수집 하는 방법이 다르다. NewsCollectorService 구현체에서 수집 방법 구현
@Service
public class CollectorDispatcher {

    private final Map<String, NewsCollectorService> collectors;

    public CollectorDispatcher(Map<String, NewsCollectorService> collectors) {
        this.collectors = collectors;
    }

    public List<ParsedNewsPreview> collectPreviews(NewsCollectionJob job) {
        String platform = job.getNewsSite().toUpperCase();

        NewsCollectorService collector = collectors.get(platform);

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported News Site: " + platform);
        }

        return collector.collectPreviews(job);
    }

    public List<ParsedNewsArticle> collectArticles(NewsCollectionJob job) throws URISyntaxException {
        String platform = job.getNewsSite().toUpperCase();

        NewsCollectorService collector = collectors.get(platform);

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported News Site: " + platform);
        }

        return collector.collectArticles(job);
    }

}
