package com.part2.monew.controller;

import com.part2.monew.service.SpringBatch;
import com.part2.monew.service.implement.NewsArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final SpringBatch springBatch;
    private final NewsArticleService newsArticleService;

    @PostMapping("/news-collection/execute")
    public ResponseEntity<Map<String, Object>> executeNewsCollection() {
        try {
            log.info("수동 뉴스 수집 배치 실행 요청");

            springBatch.executeNewsCollectionBatch();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "뉴스 수집 배치가 성공적으로 실행되었습니다.",
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            log.error("뉴스 수집 배치 실행 중 오류 발생", e);
            
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "뉴스 수집 배치 실행 중 오류가 발생했습니다: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/news-backup/execute")
    public ResponseEntity<Map<String, Object>> executeNewsBackup() {
        try {
            log.info("수동 뉴스 백업 배치 실행 요청");

            newsArticleService.executeDailyNewsBackupBatch();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "뉴스 백업 배치가 성공적으로 실행되었습니다.",
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            log.error("뉴스 백업 배치 실행 중 오류 발생", e);
            
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "뉴스 백업 배치 실행 중 오류가 발생했습니다: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/news-backup/date")
    public ResponseEntity<Map<String, Object>> backupNewsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            log.info("특정 날짜 뉴스 백업 실행 요청: {}", date);

            newsArticleService.backupDataByDate(date);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", String.format("%s 날짜의 뉴스 백업이 성공적으로 실행되었습니다.", date),
                "date", date.toString(),
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            log.error("특정 날짜 뉴스 백업 실행 중 오류 발생: {}", date, e);
            
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", String.format("%s 날짜의 뉴스 백업 중 오류가 발생했습니다: %s", date, e.getMessage()),
                "date", date.toString(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> getBatchStatus() {
        return ResponseEntity.ok(Map.of(
            "batchEnabled", true,
            "message", "배치 시스템이 활성화되어 있습니다.",
            "timestamp", System.currentTimeMillis()
        ));
    }
} 