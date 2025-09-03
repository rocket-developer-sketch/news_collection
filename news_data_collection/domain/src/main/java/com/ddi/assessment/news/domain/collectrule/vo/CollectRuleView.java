package com.ddi.assessment.news.domain.collectrule.vo;

import java.time.LocalDateTime;

public record CollectRuleView(
        Long ruleId,
        String keyword,
        String siteName,
        String interval,
        LocalDateTime createdAt,
        boolean active
) {}
