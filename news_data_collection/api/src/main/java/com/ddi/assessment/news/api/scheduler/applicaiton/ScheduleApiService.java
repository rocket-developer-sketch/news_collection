package com.ddi.assessment.news.api.scheduler.applicaiton;

import com.ddi.assessment.news.api.scheduler.dto.NewScheduleRequest;
import com.ddi.assessment.news.api.scheduler.dto.UpdateScheduleRequest;


public interface ScheduleApiService {
    void registerSchedules(Long userId, NewScheduleRequest request);
    void updateSchedule(UpdateScheduleRequest request);
    void deleteSchedule(Long configId);
}
