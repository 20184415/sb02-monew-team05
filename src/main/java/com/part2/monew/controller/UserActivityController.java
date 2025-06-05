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
@RequiredArgsConstructor
@RequestMapping("/api/user-activities")
public class UserActivityController {

  private final UserActivityService userActivityService;

  @GetMapping("/{userId}")
  public ResponseEntity<UserActivityResponse> getUserActivity(@PathVariable UUID userId) {
    return ResponseEntity.ok(userActivityService.getUserActivity(userId));
  }
}
