package com.ddi.assessment.news.domain.article.vo;

import java.util.List;

public record RegisterScheduleCommand(
    Long ruleId,
    Long keywordId,
    Long siteId,
    String keyword,
    String newsSite,
    String siteUrl,
    String cronExpression,
    List<CollectedNewsArticle> articles
) {}
