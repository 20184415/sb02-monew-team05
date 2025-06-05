package com.part2.monew.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserActivityController {

    @GetMapping({"/user-activities/{userId}", "/user-activities/"})
    public ResponseEntity<Map<String, Object>> getUserActivities(@PathVariable(required = false) UUID userId) {
        
        // 임시로 빈 사용자 활동 반환 (null 값 제거)
        String userIdString = userId != null ? userId.toString() : "anonymous";
        Map<String, Object> response = Map.of(
            "userId", userIdString,
            "activities", Collections.emptyList(),
            "totalCount", 0,
            "articlesRead", 0,
            "commentsWritten", 0
        );
        
        return ResponseEntity.ok(response);
    }
} 