package com.ddi.assessment.news.domain.interval.service;

import com.ddi.assessment.news.domain.interval.entity.JpaInterval;
import com.ddi.assessment.news.domain.interval.vo.CronExpQuery;
import com.ddi.assessment.news.domain.interval.vo.ExistingInterval;
import com.ddi.assessment.news.domain.interval.vo.IntervalCronExp;

import java.util.Arrays;
import java.util.Set;

public interface IntervalService {
    JpaInterval getInterval(String interval);
    IntervalCronExp getCronExp(CronExpQuery query);
    Set<ExistingInterval> getAllNewsSites();
}
