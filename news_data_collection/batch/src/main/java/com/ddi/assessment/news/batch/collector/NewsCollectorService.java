package com.ddi.assessment.news.batch.collector;

import com.ddi.assessment.news.batch.collector.dto.ParsedNewsArticle;
import com.ddi.assessment.news.batch.collector.dto.ParsedNewsPreview;
import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;

import java.net.URISyntaxException;
import java.util.List;

public interface NewsCollectorService {

    default List<ParsedNewsPreview> collectPreviews(NewsCollectionJob job) {
        throw new UnsupportedOperationException("This platform does not support collecting listed news information.");
    }

    default List<ParsedNewsArticle> collectArticles(NewsCollectionJob job) throws URISyntaxException {
        throw new UnsupportedOperationException("This platform does not support collecting detailed news information.");
    }

}
