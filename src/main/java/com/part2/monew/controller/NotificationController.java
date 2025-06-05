package com.part2.monew.controller;

import com.part2.monew.dto.request.NotificationCursorRequest;
import com.part2.monew.dto.response.CursorPageResponse;
import com.part2.monew.dto.response.NotificationResponse;
import com.part2.monew.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaitons")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PatchMapping("")
    public ResponseEntity<Void> updated_AllNotifications(@RequestHeader("MoNew-Request-User-ID") UUID userId){
        notificationService.updatedAll(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> updated_Notifications(
        @RequestHeader("MoNew-Request-User-ID") UUID userId,
        @PathVariable UUID notificationId){
        notificationService.updated(notificationId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<CursorPageResponse<NotificationResponse>> info_Notification(
        @ModelAttribute NotificationCursorRequest request,
        @RequestHeader("MoNew-Request-User-ID") UUID userId
    ){

        CursorPageResponse<NotificationResponse> result = notificationService.getNoConfirmedNotifications(userId, request);
        return ResponseEntity.ok(result);
    }

}