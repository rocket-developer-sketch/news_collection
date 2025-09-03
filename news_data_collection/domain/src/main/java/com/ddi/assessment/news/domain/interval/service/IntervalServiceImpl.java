package com.ddi.assessment.news.domain.interval.service;

import com.ddi.assessment.news.domain.interval.entity.JpaInterval;
import com.ddi.assessment.news.domain.interval.repository.JpaIntervalRepository;
import com.ddi.assessment.news.domain.interval.vo.CronExpQuery;
import com.ddi.assessment.news.domain.interval.vo.ExistingInterval;
import com.ddi.assessment.news.domain.interval.vo.IntervalCronExp;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IntervalServiceImpl implements IntervalService {

    private final JpaIntervalRepository jpaIntervalRepository;

    public IntervalServiceImpl(JpaIntervalRepository jpaIntervalRepository) {
        this.jpaIntervalRepository = jpaIntervalRepository;
    }


    @Override
    public JpaInterval getInterval(String interval) {

        Optional<JpaInterval> intervalOrEmpty = jpaIntervalRepository.findByLabel(interval);

        if(intervalOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("Wrong Interval Expression");
        }

        return intervalOrEmpty.get();

    }

    @Override
    public IntervalCronExp getCronExp(CronExpQuery query) {

        JpaInterval jpaInterval = getInterval(query.interval());

        return new IntervalCronExp(jpaInterval.getId(), jpaInterval.getCronExpression());

    }

    // 헐 여기 이름 바꿔야 해... getAllIntervals 라고!!
    @Override
    public Set<ExistingInterval> getAllNewsSites() {

        return jpaIntervalRepository.findAll().stream()
                .map(it -> new ExistingInterval(it.getLabel()))
                .collect(Collectors.toSet());

    }
}
