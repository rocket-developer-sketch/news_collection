package com.ddi.assessment.news.api.scheduler.dto;

public record NewScheduleApiRequest(
    Long ruleId,
    Long keywordId,
    Long siteId,
    String keyword,
    String newsSite,
    String siteUrl,
    String cronExpression
) {}
