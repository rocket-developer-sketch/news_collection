package com.ddi.assessment.news.batch.dto;

public record UpdateKeywordScheduleRequest (
        Long ruleId,
        Long keywordId,
        Long siteId,
        String keyword,
        String newsSite,
        String siteUrl,
        String cronExpression,
        Boolean isActive
) {}
