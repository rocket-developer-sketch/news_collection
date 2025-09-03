package com.ddi.assessment.news.api.scheduler.dto;

import java.time.LocalDateTime;

public record ScheduleQueryResponse (
    Long configId,
    String keyword,
    String siteName,
    String interval,
    LocalDateTime createdAt,
    boolean active
) {}
