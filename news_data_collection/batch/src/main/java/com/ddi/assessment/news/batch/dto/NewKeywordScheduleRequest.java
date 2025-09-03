package com.ddi.assessment.news.batch.dto;

public record NewKeywordScheduleRequest (
        Long ruleId,
        Long keywordId,
        Long siteId,
        String keyword,
        String newsSite,
        String siteUrl,
        String cronExpression
) {}
