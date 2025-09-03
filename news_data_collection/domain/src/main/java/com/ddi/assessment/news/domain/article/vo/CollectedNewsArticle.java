package com.ddi.assessment.news.domain.article.vo;

import java.time.LocalDateTime;

public record CollectedNewsArticle(
    Long newsId,
    String keyword,
    String siteName,
    String newsUrl,
    String title,
    String content,
    LocalDateTime publishedAt
) {}
