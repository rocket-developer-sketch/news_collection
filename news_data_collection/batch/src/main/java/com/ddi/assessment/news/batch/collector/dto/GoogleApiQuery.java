package com.ddi.assessment.news.batch.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GoogleApiQuery {
    List<GoogleApiNextPage> nextPage;
}
