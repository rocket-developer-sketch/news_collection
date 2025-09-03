package com.ddi.assessment.news.api.scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateScheduleRequest(
    @NotNull(message = "설정 정보는 필수입니다")
    Long ruleId,
    @NotBlank(message = "키워드는 필수입니다")
    @Size(min = 1, max = 100, message = "키워드는 1자 이상 100자 이하입니다")
    String keyword,
    @NotBlank(message = "뉴스 사이트는 필수입니다")
    String siteName,
    @NotBlank(message = "주기는 필수입니다")
    String interval,
    @NotNull(message = "활성화 여부는 필수입니다")
    Boolean isActive
) {}
