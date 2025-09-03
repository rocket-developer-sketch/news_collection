package com.ddi.assessment.news.api.scheduler.client;

import com.ddi.assessment.news.api.config.BatchApiProperties;
import com.ddi.assessment.news.api.scheduler.dto.NewScheduleApiRequest;
import com.ddi.assessment.news.api.scheduler.dto.UpdateScheduleApiRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ScheduleApiClient {

    private final RestTemplate restTemplate;
    private final BatchApiProperties batchApiProperties;

    public ScheduleApiClient(RestTemplate restTemplate, BatchApiProperties batchApiProperties) {
        this.restTemplate = createTimeoutRestTemplate();
        this.batchApiProperties = batchApiProperties;
    }

    public void registerJobs(List<NewScheduleApiRequest> request) {
        String url = batchApiProperties.getBaseUrl() + "/schedule/news/bulk";

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<NewScheduleApiRequest>> entity = new HttpEntity<>(request, headers);
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void register(NewScheduleApiRequest request) {
        String url = batchApiProperties.getBaseUrl() + "/schedule/news";
        HttpEntity<NewScheduleApiRequest> entity = new HttpEntity<>(request, createHeaders());
        restTemplate.postForEntity(url, entity, Void.class);
    }

    public void update(UpdateScheduleApiRequest request) {
        String url = batchApiProperties.getBaseUrl() + "/schedule/" + request.ruleId();
        HttpEntity<UpdateScheduleApiRequest> entity = new HttpEntity<>(request, createHeaders());
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    public void delete(Long ruleId) {
        String url = batchApiProperties.getBaseUrl() + "/schedule/" + ruleId;
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Internal-Secret", batchApiProperties.getInternalSecret());
        return headers;
    }

    private RestTemplate createTimeoutRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }

}
