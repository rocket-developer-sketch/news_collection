package com.ddi.assessment.news.batch.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ParsedNewsPreview implements Serializable {
    private static final long serialVersionUID = 1721990400123456L;

    private String url;
    private Long newsId;
    private LocalDate publishedDate;
}
