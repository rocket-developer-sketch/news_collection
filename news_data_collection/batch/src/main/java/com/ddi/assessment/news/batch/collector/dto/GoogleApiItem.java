package com.ddi.assessment.news.batch.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleApiItem {
    String title;
    String link;
    String snippet;
}
