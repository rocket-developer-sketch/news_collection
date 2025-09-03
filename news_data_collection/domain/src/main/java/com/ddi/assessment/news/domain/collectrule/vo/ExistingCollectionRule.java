package com.ddi.assessment.news.domain.collectrule.vo;

public record ExistingCollectionRule(
    Long userId,
    Long ruleId,
    Long keywordId,
    Long siteId,
    Long intervalId,
    Boolean isActive
) {}
