package com.part2.monew.controller;

import com.part2.monew.dto.ArticleResponseDto;
import com.part2.monew.dto.NewsArticleResponseDto;
import com.part2.monew.dto.PaginatedResponseDto;
import com.part2.monew.dto.response.ArticleListResponseDto;
import com.part2.monew.dto.FilterDto;
import com.part2.monew.dto.RequestCursorDto;
import com.part2.monew.dto.RestoreResultDto;
import com.part2.monew.service.implement.NewsArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final NewsArticleService newsArticleService;

    public ArticleController(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
    }

    @GetMapping
    public ResponseEntity<ArticleListResponseDto<NewsArticleResponseDto>> getArticles(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "interestId", required = false) String interestId,
        @RequestParam(value = "sourceIn", required = false) List<String> sourceIn,
        @RequestParam(value = "publishDateFrom", required = false) String publishDateFrom,
        @RequestParam(value = "publishDateTo", required = false) String publishDateTo,
        @RequestParam(value = "orderBy") String orderBy,
        @RequestParam(value = "direction") String direction,
        @RequestParam(value = "cursor", required = false) String cursor,
        @RequestParam(value = "after", required = false) String after,
        @RequestParam(value = "limit") Integer limit,
        @RequestHeader(value = "Monew-Request-User-ID") String userId) {
        
        try {
           
            
      
            Timestamp publishDateFromTs = null;
            Timestamp publishDateToTs = null;
            Timestamp afterTs = null;
            
            if (publishDateFrom != null && !publishDateFrom.isEmpty()) {
                publishDateFromTs = Timestamp.valueOf(LocalDateTime.parse(publishDateFrom, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            if (publishDateTo != null && !publishDateTo.isEmpty()) {
                publishDateToTs = Timestamp.valueOf(LocalDateTime.parse(publishDateTo, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            if (after != null && !after.isEmpty()) {
                afterTs = Timestamp.valueOf(LocalDateTime.parse(after, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            // UUID 파싱
            UUID interestUuid = null;
            if (interestId != null && !interestId.isEmpty()) {
                interestUuid = UUID.fromString(interestId);
            }
            
            // 빈 문자열을 null로 변환 (조건 무시)
            if (keyword != null && keyword.trim().isEmpty()) keyword = null;
            if (orderBy != null && orderBy.trim().isEmpty()) orderBy = "publishDate";
            if (direction != null && direction.trim().isEmpty()) direction = "DESC";
            
            // FilterDto와 RequestCursorDto 생성
            FilterDto filterDto = new FilterDto(keyword, interestUuid, sourceIn, publishDateFromTs, publishDateToTs);
            RequestCursorDto cursorDto = new RequestCursorDto(orderBy, direction, cursor, afterTs, null, limit);
            
            // 실제 복잡한 서비스 호출
            var result = newsArticleService.getArticlesForSwagger(filterDto, cursorDto, userId);
            
            System.out.println("조회 결과: " + result.getSize() + "개");
            System.out.println("=== 복잡한 쿼리 실행 완료 ===");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("복잡한 쿼리 실행 오류: " + e.getMessage());
            
            // 오류 발생시 빈 응답 반환
            ArticleListResponseDto errorResponse = ArticleListResponseDto.builder()
                .content(List.of())
                .nextCursor(null)
                .nextAfter(null)
                .size(0)
                .totalElements(0L)
                .hasNext(false)
                .build();
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<String> registerArticleView(
        @PathVariable String articleId,
        @RequestHeader(value = "Monew-Request-User-ID") String userId) {
        
        try {
            System.out.println("=== 조회수 증가 시작 ===");
            System.out.println("articleId: " + articleId);
            System.out.println("userId: " + userId);
            
            UUID articleUuid = UUID.fromString(articleId);
            UUID userUuid = UUID.fromString(userId);
            newsArticleService.incrementViewCount(articleUuid, userUuid);
            
            System.out.println("=== 조회수 증가 완료 ===");
            return ResponseEntity.ok("조회수 증가 완료");
        } catch (Exception e) {
            System.err.println("조회수 증가 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("조회수 증가 오류: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        try {
            System.out.println("=== 테스트 엔드포인트 시작 ===");
            
            // 간단한 조회 테스트
            var articles = newsArticleService.getNewsArticleRepository().findByIsDeletedFalse(
                org.springframework.data.domain.PageRequest.of(0, 5)
            );
            
            System.out.println("=== 테스트 엔드포인트 완료 ===");
            return ResponseEntity.ok("Test endpoint 작동함! 총 " + articles.size() + "개 기사 발견");
        } catch (Exception e) {
            System.err.println("Test endpoint 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Test endpoint 오류: " + e.getMessage());
        }
    }

    /**
     * 뉴스 기사 복구
     */
    @GetMapping("/restore")
    public ResponseEntity<List<RestoreResultDto>> restoreArticles(
        @RequestParam("from") Timestamp from,
        @RequestParam("to") Timestamp to) {
        
        List<RestoreResultDto> restoreResults = newsArticleService.restoreArticles(from, to);
        return ResponseEntity.ok(restoreResults);
    }

         
     @GetMapping("/sources")
     public ResponseEntity<List<String>> getSource() {
         List<String> sources = newsArticleService.getNewsSources();
         return ResponseEntity.ok(sources);
     }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> delete(@PathVariable String articleId) {
        UUID uuid = UUID.fromString(articleId);
        newsArticleService.softDeleteArticle(uuid);
        return ResponseEntity.noContent().build();
    }

    //하드
    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDelete(@PathVariable String articleId) {
        UUID uuid = UUID.fromString(articleId);
        newsArticleService.hardDeleteArticle(uuid);
        return ResponseEntity.noContent().build();
    }

    
}
