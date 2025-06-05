package com.part2.monew.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    /**
     * 애플리케이션 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            status.put("application", "running");
            status.put("timestamp", System.currentTimeMillis());
            
            // 데이터베이스 연결 테스트
            try (Connection connection = dataSource.getConnection()) {
                status.put("database", "connected");
                status.put("databaseUrl", connection.getMetaData().getURL());
                
                // 테이블 목록 조회
                List<String> tables = new ArrayList<>();
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
                status.put("tables", tables);
                
                // Interest 테이블이 있으면 데이터 개수 조회
                if (tables.contains("interests")) {
                    try (var stmt = connection.createStatement();
                         var rs = stmt.executeQuery("SELECT COUNT(*) FROM interests")) {
                        if (rs.next()) {
                            status.put("interestCount", rs.getInt(1));
                        }
                    }
                }
                
                // NewsArticle 테이블이 있으면 데이터 개수 조회
                if (tables.contains("news_articles")) {
                    try (var stmt = connection.createStatement();
                         var rs = stmt.executeQuery("SELECT COUNT(*) FROM news_articles")) {
                        if (rs.next()) {
                            status.put("newsArticleCount", rs.getInt(1));
                        }
                    }
                }
                
            } catch (Exception e) {
                status.put("database", "error");
                status.put("databaseError", e.getMessage());
                log.error("Database connection error", e);
            }
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            status.put("error", e.getMessage());
            log.error("Health check error", e);
            return ResponseEntity.internalServerError().body(status);
        }
    }
} 