package com.ddi.assessment.news.domain.interval.vo;

public record IntervalCronExp (
        Long intervalId,
        String cronExpression
) {}
