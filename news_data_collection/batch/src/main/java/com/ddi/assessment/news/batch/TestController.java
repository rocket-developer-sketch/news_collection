package com.ddi.assessment.news.batch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {
    @GetMapping("/healthcheck")
    public Map<String, String> healthCheck() {
        return Map.of("status", "ok");
    }
}
