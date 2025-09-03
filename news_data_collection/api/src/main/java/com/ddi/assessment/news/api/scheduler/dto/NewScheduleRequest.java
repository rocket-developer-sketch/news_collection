package com.ddi.assessment.news.api.scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NewScheduleRequest(
    @NotEmpty(message = "키워드는 최소 1개 이상이어야 합니다")
    List<@NotBlank String> keyword,
    @NotEmpty(message = "뉴스 사이트는 최소 1개 이상이어야 합니다")
    List<@NotBlank String> newsSite,
    @NotBlank(message = "주기는 필수입니다")
    String interval
) {}
