package com.part2.monew.controller;

import com.part2.monew.entity.NewsArticle;
import com.part2.monew.service.SimpleNewsCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/batch/test")
public class BatchTestController {

    private final SimpleNewsCollectionService simpleNewsCollectionService;

    public BatchTestController(SimpleNewsCollectionService simpleNewsCollectionService) {
        this.simpleNewsCollectionService = simpleNewsCollectionService;
    }


    @GetMapping("/simple-news-collection")
    public ResponseEntity<Map<String, Object>> executeSimpleNewsCollection() {
        log.info("=== 간단한 키워드 매칭 배치 수동 실행 시작 ===");
        
        try {
            long startTime = System.currentTimeMillis();
            
            List<NewsArticle> collectedArticles = simpleNewsCollectionService.collectNewsWithSimpleKeywordMatching();
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "간단한 키워드 매칭 배치 실행 완료");
            result.put("collectedCount", collectedArticles.size());
            result.put("executionTimeMs", executionTime);
            result.put("executionTimeSec", executionTime / 1000.0);
            
            // 샘플 기사 정보 (최대 3개)
            if (!collectedArticles.isEmpty()) {
                List<Map<String, Object>> samples = collectedArticles.stream()
                        .limit(3)
                        .map(article -> {
                            Map<String, Object> sample = new HashMap<>();
                            sample.put("title", article.getTitle());
                            sample.put("source", article.getSourceIn());
                            sample.put("publishedDate", article.getPublishedDate());
                            return sample;
                        })
                        .toList();
                result.put("samples", samples);
            }
            
            log.info("=== 간단한 키워드 매칭 배치 수동 실행 완료: {}개 기사, {}ms ===", 
                    collectedArticles.size(), executionTime);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "간단한 키워드 매칭 배치 실행 실패: " + e.getMessage());
            errorResult.put("collectedCount", 0);
            
            return ResponseEntity.status(500).body(errorResult);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBatchStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("success", true);
        status.put("message", "배치 테스트 컨트롤러 정상 동작");
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }
} 