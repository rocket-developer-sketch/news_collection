package com.ddi.assessment.news.domain.collectrule.vo;

public record UpdateCollectionRuleCommand(
    Long configId,
    Long intervalId,
    String cronExpression,
    Boolean isActive
) {}
