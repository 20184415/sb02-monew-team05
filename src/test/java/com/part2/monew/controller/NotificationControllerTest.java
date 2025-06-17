package com.part2.monew.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part2.monew.dto.request.NotificationCursorRequest;
import com.part2.monew.dto.response.CursorPageResponse;
import com.part2.monew.dto.response.NotificationResponse;
import com.part2.monew.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updated_allNotifications() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(patch("/api/notifications")
                        .header("Monew-Request-User-ID", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(notificationService).updatedAll(userId);
    }

    @Test
    void updated_singleNotification() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        mockMvc.perform(patch("/api/notifications/" + notificationId)
                        .header("Monew-Request-User-ID", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(notificationService).updated(notificationId, userId);
    }

    @Test
    void notifications_info() throws Exception {
        UUID notiId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        NotificationResponse mockNotification = new NotificationResponse(
                notiId,
                now,
                now,
                false,
                userId,
                "테스트 알림",
                "COMMENT",
                resourceId
        );

        CursorPageResponse<NotificationResponse> response = CursorPageResponse.of(
                List.of(mockNotification),
                "cursor123",
                "after123",
                100L,
                true
        );

        Mockito.when(notificationService.getNoConfirmedNotifications(eq(userId), any(NotificationCursorRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/notifications")
                        .header("Monew-Request-User-ID", userId.toString())
                        .param("cursor", "")
                        .param("after", "")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(notiId.toString()))
                .andExpect(jsonPath("$.content[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].content").value("테스트 알림"))
                .andExpect(jsonPath("$.content[0].resourceType").value("COMMENT"))
                .andExpect(jsonPath("$.content[0].resourceId").value(resourceId.toString()))
                .andExpect(jsonPath("$.content[0].confirmed").value(false))
                .andExpect(jsonPath("$.nextCursor").value("cursor123"))
                .andExpect(jsonPath("$.nextAfter").value("after123"))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(100))
                .andExpect(jsonPath("$.hasNext").value(true));
    }
}
