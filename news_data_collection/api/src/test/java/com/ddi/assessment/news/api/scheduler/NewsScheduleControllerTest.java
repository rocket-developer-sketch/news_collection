package com.ddi.assessment.news.api.scheduler;


import com.ddi.assessment.news.api.scheduler.applicaiton.ScheduleApiService;
import com.ddi.assessment.news.api.scheduler.applicaiton.ScheduleService;
import com.ddi.assessment.news.api.scheduler.controller.NewsScheduleController;
import com.ddi.assessment.news.api.scheduler.dto.NewScheduleRequest;
import com.ddi.assessment.news.api.scheduler.dto.UpdateScheduleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("사용자 스케줄 설정 API 테스트")
@AutoConfigureMockMvc(addFilters = false) // 인증 필요 없이 api 호출 하기 위해 Filter 제거
@WebMvcTest(NewsScheduleController.class)
public class NewsScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleApiService scheduleApiService;

    @MockBean
    private ScheduleService scheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /api/v1/schedule 호출 시")
    class RegisterSchedule {
        // todo 인증 객체 때문에 실패 중. UserDetails 구현
//        @Test
//        @WithMockUser(username = "testUser1")
//        @DisplayName("정상 요청 일 때 스케줄 최초 등록 성공")
//        void success() throws Exception {
//            NewScheduleRequest request = new NewScheduleRequest(
//                    List.of("삼성전자", "갤럭시"),
//                    List.of("NAVER"),
//                    "15분마다"
//            );
//
//            mockMvc.perform(post("/api/v1/schedule")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isOk());
//        }
        
//        @Test
//        @DisplayName("필드 keyword 누락 일 때 실패")
//        void fail_keyword_blank() throws Exception {
//            NewScheduleRequest request = new NewScheduleRequest(
//                    List.of(" ", "AI"),
//                    List.of("NAVER"),
//                    "10분마다"
//            );
//
//            mockMvc.perform(post("/api/v1/schedule")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest());
//        }
    }

    @Nested
    @DisplayName("PUT /api/v1/schedule/{configId} 호출 시 ")
    class UpdateSchedule {

        @Test
        @DisplayName("정상 요청 일 때 기존 스케줄 변경 성공")
        void success() throws Exception {
            UpdateScheduleRequest request = new UpdateScheduleRequest(1L, "SK하이닉스", "NAVER", "10분마다", true);

            mockMvc.perform(put("/api/v1/schedule/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("필드 isActive 누락 일 때 실패")
        void fail_isActive_null() throws Exception {
            // 수동으로 JSON 구성하여 isActive null 전달
            String json = """
                {
                    "configId": 1,
                    "keyword": ["삼성전자"],
                    "newsSite": ["NAVER"],
                    "interval": "10분마다",
                    "isActive": null
                }
            """;

            mockMvc.perform(put("/api/v1/schedule/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/schedule/{configId}")
    class DeleteSchedule {

        @Test
        @DisplayName("스케줄 삭제 요청 성공은 빈 body")
        void success() throws Exception {
            mockMvc.perform(delete("/api/v1/schedule/1"))
                    .andExpect(status().isNoContent());
        }
    }
}
