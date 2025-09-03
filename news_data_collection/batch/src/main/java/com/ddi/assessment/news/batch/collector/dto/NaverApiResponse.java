package com.ddi.assessment.news.batch.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NaverApiResponse {
    private String title;
    private String link;
    private String originalLink;
    private String description;
    private LocalDateTime pubDate;
}
