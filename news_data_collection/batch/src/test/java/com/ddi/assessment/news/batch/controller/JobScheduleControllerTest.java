package com.ddi.assessment.news.batch.controller;

import com.ddi.assessment.news.batch.dto.NewKeywordScheduleRequest;
import com.ddi.assessment.news.batch.dto.UpdateKeywordScheduleRequest;
import com.ddi.assessment.news.batch.job.JobSchedulerService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("JobScheduleController API 테스트")
@WebMvcTest(JobScheduleController.class)
class JobScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobSchedulerService jobScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /schedule/news/bulk 호출 시 jobs 일괄 등록 후 success 반환")
    void shouldRegisterJobs_whenBulkPostRequestSent() throws Exception {

        List<NewKeywordScheduleRequest> requests = List.of(
                new NewKeywordScheduleRequest(1L, 2L, 1L, "인공지능", "DAUM", "http://daum.example.com", "0 */15 * * * ?"),
                new NewKeywordScheduleRequest(2L, 3L, 2L, "주식", "NAVER", "http://naver.example.com", "0 0 * * * ?")
        );

        verify(jobScheduleService, never()).registerJobs(requests);


        mockMvc.perform(post("/schedule/news/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    @DisplayName("PUT /schedule/{id} 호출 시 job 업데이트 후 success 반환")
    void shouldUpdateJob_whenPutRequestSent() throws Exception {

        UpdateKeywordScheduleRequest request = new UpdateKeywordScheduleRequest(
                1L, null, null, "엔비디아", "NAVER", null, "0 */15 * * * ?", true
        );

        verify(jobScheduleService, never()).updateJob(request);

        mockMvc.perform(put("/schedule/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    @DisplayName("DELETE /schedule/{id} 호출 시 job 삭제 후 success 반환")
    void shouldDeleteJob_whenDeleteRequestSent() throws Exception {

        verify(jobScheduleService, never()).deleteJob(10L);

        mockMvc.perform(delete("/schedule/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }
}
