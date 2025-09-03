package com.ddi.assessment.news.api.scheduler.applicaiton;


import com.ddi.assessment.news.api.dto.PageResponse;
import com.ddi.assessment.news.domain.collectrule.vo.CollectRuleView;

public interface ScheduleService {
    PageResponse<CollectRuleView> getSchedules(int pageNo, int size);
    PageResponse<CollectRuleView> getUserCollectConfigs(Long userId, int pageNo, int size);
}
