package com.ddi.assessment.news.batch.collector.naver;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.NewsCollectorService;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service("NAVER")
public class NaverCollectorServiceImpl implements NewsCollectorService {

    private final NaverNewsParser parser;

    public NaverCollectorServiceImpl(NaverNewsParser parser) {
        this.parser = parser;
    }

    public List<ParsedNewsArticle> collectArticles(NewsCollectionJob job) throws URISyntaxException {
        return parser.extractArticles(job);
    }
}
