package com.ddi.assessment.news.batch.job;

import com.ddi.assessment.news.batch.job.dto.NewsCollectionJob;
import org.springframework.batch.core.Job;

public interface NewsJobStrategy {
    String getNewsSiteName();
    Job buildJob(NewsCollectionJob job);
}
