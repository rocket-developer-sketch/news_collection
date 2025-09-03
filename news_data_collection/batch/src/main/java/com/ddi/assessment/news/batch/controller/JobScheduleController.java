package com.ddi.assessment.news.batch.controller;

import com.ddi.assessment.news.batch.dto.NewKeywordScheduleRequest;
import com.ddi.assessment.news.batch.dto.UpdateKeywordScheduleRequest;
import com.ddi.assessment.news.batch.job.JobSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class JobScheduleController {

    private final JobSchedulerService jobSchedulerService;

    public JobScheduleController(JobSchedulerService jobSchedulerService) {
        this.jobSchedulerService = jobSchedulerService;
    }

    @PostMapping("/news")
    public ResponseEntity<String> scheduleJob(@RequestBody NewKeywordScheduleRequest request) {

        jobSchedulerService.registerJob(request);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/news/bulk")
    public ResponseEntity<String> scheduleJobs(@RequestBody List<NewKeywordScheduleRequest> request) {

        jobSchedulerService.registerJobs(request);

        return ResponseEntity.ok("success");
    }

    @PutMapping("/{configId}")
    public ResponseEntity<String> update(@RequestBody UpdateKeywordScheduleRequest request) {

        jobSchedulerService.updateJob(request);

        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/{configId}")
    public ResponseEntity<String> delete(@PathVariable Long configId) {

        jobSchedulerService.deleteJob(configId);

        return ResponseEntity.ok("success");
    }

}
