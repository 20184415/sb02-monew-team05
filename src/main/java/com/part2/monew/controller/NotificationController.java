package com.part2.monew.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(defaultValue = "50") int limit) {
        
        // 임시로 빈 알림 목록 반환
        Map<String, Object> response = Map.of(
            "content", Collections.emptyList(),
            "totalElements", 0,
            "size", 0,
            "hasNext", false
        );
        
        return ResponseEntity.ok(response);
    }
} 