package com.part2.monew.service;

import com.part2.monew.dto.request.NotificationCursorRequest;
import com.part2.monew.entity.Notification;
import com.part2.monew.entity.User;
import com.part2.monew.repository.NotificationRepository;
import com.part2.monew.repository.UserRepository;
import com.part2.monew.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Transactional
@ActiveProfiles("test")
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private UUID resourceId;
    private String content;
    private String resourceType;

    private Notification notification;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setNickname("테스트유저");
        user.setPassword("password");

        resourceId = UUID.randomUUID();
        content = "댓글이 달렸습니다.";
        resourceType = "COMMENT";

        notification = new Notification(user, content, resourceType, resourceId);
        notification.setId(UUID.randomUUID());
        notification.setConfirmed(false);
    }

    @Test
    void createNotification_success() {
        // given
        Notification expected = new Notification(user, content, resourceType, resourceId);
        given(notificationRepository.save(any(Notification.class))).willReturn(expected);

        // when
        Notification result = notificationService.createNotification(user, content, resourceType, resourceId);

        // then
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(content, result.getContent());
        assertEquals(resourceType, result.getResourceType());
        assertEquals(resourceId, result.getResourceId());
        assertFalse(result.isConfirmed());
    }

    @Test
    void updated_success() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setUser(new User());
        notification.getUser().setId(userId);
        notification.setConfirmed(false);

        given(notificationRepository.findById(notificationId)).willReturn(java.util.Optional.of(notification));

        notificationService.updated(notificationId, userId);

        assertTrue(notification.isConfirmed());
    }

    @Test
    void updatedAll_success() {
        UUID userId = UUID.randomUUID();
        Notification n1 = new Notification();
        Notification n2 = new Notification();
        n1.setConfirmed(false);
        n2.setConfirmed(false);

        given(notificationRepository.findByUserIdAndConfirmedFalse(userId)).willReturn(List.of(n1, n2));

        notificationService.updatedAll(userId);

        assertTrue(n1.isConfirmed());
        assertTrue(n2.isConfirmed());
    }

    @Test
    void getNoConfirmedNotifications_withoutCursor_success() {
        UUID userId = UUID.randomUUID();
        Notification n1 = new Notification();
        n1.setId(UUID.randomUUID());
        n1.setUser(new User());
        n1.getUser().setId(userId);
        n1.setConfirmed(false);

        given(notificationRepository.findTop51ByUserIdAndConfirmedFalseOrderByCreatedAtDesc(userId))
                .willReturn(List.of(n1));
        given(notificationRepository.countByUserIdAndConfirmedFalse(userId)).willReturn(1L);

        NotificationCursorRequest request = new NotificationCursorRequest(null, null, 10);
        var result = notificationService.getNoConfirmedNotifications(userId, request);

        assertEquals(1, result.content().size());
        assertFalse(result.hasNext());
        assertEquals(1L, result.totalElements());
    }





}