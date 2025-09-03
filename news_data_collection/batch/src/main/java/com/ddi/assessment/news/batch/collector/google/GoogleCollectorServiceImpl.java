package com.ddi.assessment.news.batch.collector.google;

import com.ddi.assessment.news.batch.collector.NewsCollectorService;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service("GOOGLE")
public class GoogleCollectorServiceImpl implements NewsCollectorService {
    private final GoogleNewsParser parser;

    public GoogleCollectorServiceImpl(GoogleNewsParser parser) {
        this.parser = parser;
    }

    public List<ParsedNewsArticle> collectArticles(NewsCollectionJob job) throws URISyntaxException {
        return parser.extractArticles(job);
    }
}
