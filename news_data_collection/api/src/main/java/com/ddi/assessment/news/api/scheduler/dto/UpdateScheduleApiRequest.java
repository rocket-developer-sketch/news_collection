package com.ddi.assessment.news.api.scheduler.dto;

public record UpdateScheduleApiRequest (
    Long ruleId,
    Long keywordId,
    Long siteId,
    String keyword,
    String newsSite,
    String siteUrl,
    String cronExpression,
    Boolean isActive
) {}
