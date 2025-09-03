package com.ddi.assessment.news.api.auth;

import com.ddi.assessment.news.api.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("인증 통합 테스트")
public class AuthenticationIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    ObjectMapper om = new ObjectMapper();

    private static final String API_URL = "/api/v1/schedule";

    private String validJsonBody = """
        {
          "keyword": ["youtube"],
          "newsSite": ["DAUM"],
          "interval": "30분마다"
        }
    """;

    @Nested
    @DisplayName("토큰 없이 요청 시")
    class NoToken {

        @Test
        @DisplayName("401 Unauthorized")
        void testNoToken() throws Exception {
            mockMvc.perform(post(API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJsonBody))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
        }
    }

    @Nested
    @DisplayName("잘못된 토큰으로 요청 시")
    class InvalidToken {

        @Test
        @DisplayName("401 Unauthorized")
        void testInvalidToken() throws Exception {
            mockMvc.perform(post(API_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.bearer.dummy.token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJsonBody))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("만료된 토큰으로 요청 시")
    class ExpiredToken {

        @Test
        @DisplayName("401 Unauthorized")
        void testExpiredToken() throws Exception {
            String expiredToken = createExpiredToken("unautorhizedUser");

            mockMvc.perform(post(API_URL)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validJsonBody))
                    .andExpect(status().isUnauthorized());
        }

        private String createExpiredToken(String subject) {
            LocalDateTime issuedAt = LocalDateTime.now().minusHours(2);
            LocalDateTime expiredAt = issuedAt.plusMinutes(1);

            String secret = "authentication-intergration-test-dummy-secret-token-by-subject";
            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(toDate(issuedAt))
                    .setExpiration(toDate(expiredAt))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        }

        private Date toDate(LocalDateTime ldt) {
            //return Date.from(ldt.atZone(ZoneId.of("UTC")).toInstant());
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    // todo 통합 테스트 진행 시 batch 실행 중이어야 함
//    @Nested
//    @DisplayName("유효한 토큰으로 요청 시")
//    class ValidToken {
//
//        @Test
//        @DisplayName("200 OK")
//        void testValidToken() throws Exception {
//            String accessToken = jwtTokenProvider.issueTokens(1L, "testUser", new String[]{"ROLE_USER"}).access();
//
//            mockMvc.perform(post(API_URL)
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(validJsonBody))
//                    .andExpect(status().is2xxSuccessful());
//        }
//    }
}
