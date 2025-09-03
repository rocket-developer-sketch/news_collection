package com.ddi.assessment.news.batch.job.dto;

import com.ddi.assessment.news.batch.collector.dto.ParsedArticlesHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewsCollectionJob {
    Long configId;
    Long keywordId;
    Long siteId;
    String keyword;
    String newsSite;
    String siteUrl;
    String cronExpression;

    private final ParsedArticlesHolder parsedArticlesHolder = new ParsedArticlesHolder();

}