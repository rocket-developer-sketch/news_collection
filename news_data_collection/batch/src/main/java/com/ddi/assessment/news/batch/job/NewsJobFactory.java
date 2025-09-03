package com.ddi.assessment.news.batch.job;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 뉴스 사이트에 맞는 step 진행하기 위한 job 생성
@Component
public class NewsJobFactory {
    private final Map<String, NewsJobStrategy> strategyMap;

    public NewsJobFactory(List<NewsJobStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        s -> s.getNewsSiteName().toUpperCase(),
                        Function.identity()
                ));
    }

    public Job build(NewsCollectionJob createJob) {
        String newsSiteName = createJob.getNewsSite().toUpperCase();
        NewsJobStrategy strategy = strategyMap.get(newsSiteName);

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported News Site " + newsSiteName);
        }

        return strategy.buildJob(createJob);
    }
}
