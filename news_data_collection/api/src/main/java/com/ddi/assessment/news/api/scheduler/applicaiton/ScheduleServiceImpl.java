package com.ddi.assessment.news.api.scheduler.applicaiton;

import com.ddi.assessment.news.api.dto.PageResponse;
import com.ddi.assessment.news.domain.collectrule.service.CollectionRuleService;
import com.ddi.assessment.news.domain.collectrule.vo.CollectRuleView;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final CollectionRuleService collectionRuleService;

    public ScheduleServiceImpl(CollectionRuleService collectionRuleService) {
        this.collectionRuleService = collectionRuleService;
    }

    @Override
    public PageResponse<CollectRuleView> getSchedules(int pageNo, int size) {

        Page<CollectRuleView> allConfigs = collectionRuleService.getCollectConfigs(pageNo, size);

        return new PageResponse<>(
                allConfigs.getContent(),
                allConfigs.getNumber() + 1,
                allConfigs.getSize(),
                allConfigs.getTotalElements(),
                allConfigs.getTotalPages(),
                allConfigs.isLast()
        );

    }

    @Override
    public PageResponse<CollectRuleView> getUserCollectConfigs(Long userId, int pageNo, int size) {
        Page<CollectRuleView> allConfigs = collectionRuleService.getUserCollectConfigs(userId, pageNo, size);

        return new PageResponse<>(
                allConfigs.getContent(),
                allConfigs.getNumber() + 1,
                allConfigs.getSize(),
                allConfigs.getTotalElements(),
                allConfigs.getTotalPages(),
                allConfigs.isLast()
        );
    }
}
