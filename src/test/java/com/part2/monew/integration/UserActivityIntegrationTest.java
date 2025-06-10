package com.part2.monew.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part2.monew.entity.User;
import com.part2.monew.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserActivityIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;

  private UUID userId;
  private String storedEmail;

  @BeforeEach
  void setUp() {
    storedEmail = "user_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

    User user = User.builder()
        .email(storedEmail)
        .nickname("tester")
        .password("123456")
        .active(true)
        .build();
    userRepository.save(user);
    userId = user.getId();
  }

  @Test
  @DisplayName("사용자 활동 내역 조회 성공")
  void getUserActivity_success() throws Exception {
    mockMvc.perform(get("/api/user-activities/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(storedEmail))
        .andExpect(jsonPath("$.nickname").value("tester"))
        .andExpect(jsonPath("$.subscriptions").isArray())
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.commentLikes").isArray())
        .andExpect(jsonPath("$.articleViews").isArray());
  }

  @Test
  @DisplayName("사용자 활동 내역 조회 실패 - 존재하지 않는 사용자")
  void getUserActivity_userNotFound() throws Exception {
    UUID invalidId = UUID.randomUUID();

    mockMvc.perform(get("/api/user-activities/{userId}", invalidId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("U001"))
        .andExpect(jsonPath("$.message").value("해당 사용자를 찾을 수 없습니다."))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.path").value("/api/user-activities/" + invalidId));
  }
}
