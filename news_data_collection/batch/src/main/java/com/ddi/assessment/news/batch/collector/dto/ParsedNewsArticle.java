package com.ddi.assessment.news.batch.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ParsedNewsArticle implements Serializable {
    private static final long serialVersionUID = 1721990400876543L;

    private Long newsId;
    private String keyword;
    private String siteName;
    private String newsUrl;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
}
