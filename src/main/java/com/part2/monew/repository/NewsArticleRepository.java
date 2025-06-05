package com.part2.monew.repository;

import com.part2.monew.entity.NewsArticle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, UUID> {
    
    // 기본 조회
    List<NewsArticle> findAll();
    
    // 삭제되지 않은 기사만 조회
    List<NewsArticle> findByIsDeletedFalse(Pageable pageable);
    
    // ID로 활성 기사 조회
    @Query("SELECT n FROM NewsArticle n WHERE n.id = :id AND n.isDeleted = false")
    Optional<NewsArticle> findActiveById(@Param("id") UUID id);
    
    // 최근 뉴스 조회
    @Query("SELECT n FROM NewsArticle n WHERE n.isDeleted = false ORDER BY n.publishedDate DESC")
    List<NewsArticle> findRecentNews(Pageable pageable);
    
    // 키워드로 검색 (제목 + 내용)
    @Query("SELECT n FROM NewsArticle n WHERE n.isDeleted = false AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY n.createdAt DESC")
    List<NewsArticle> findByTitleContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(@Param("keyword") String keyword, Pageable pageable);
    
    // 향상된 키워드 검색 (제목 + 내용, 정렬 옵션)
    @Query("SELECT n FROM NewsArticle n WHERE n.isDeleted = false AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY " +
           "CASE WHEN :orderBy = 'publishDate' AND :direction = 'DESC' THEN n.publishedDate END DESC, " +
           "CASE WHEN :orderBy = 'publishDate' AND :direction = 'ASC' THEN n.publishedDate END ASC, " +
           "CASE WHEN :orderBy = 'viewCount' AND :direction = 'DESC' THEN n.viewCount END DESC, " +
           "CASE WHEN :orderBy = 'viewCount' AND :direction = 'ASC' THEN n.viewCount END ASC, " +
           "CASE WHEN :orderBy = 'commentCount' AND :direction = 'DESC' THEN n.commentCount END DESC, " +
           "CASE WHEN :orderBy = 'commentCount' AND :direction = 'ASC' THEN n.commentCount END ASC, " +
           "n.createdAt DESC")
    List<NewsArticle> searchArticlesByKeywordAdvanced(
        @Param("keyword") String keyword,
        @Param("orderBy") String orderBy,
        @Param("direction") String direction,
        Pageable pageable
    );
    
    // URL 중복 체크
    boolean existsBySourceUrl(String sourceUrl);
    Optional<NewsArticle> findBySourceUrl(String sourceUrl);
    
    // 조회수 상위 기사들
    List<NewsArticle> findByIsDeletedFalseOrderByViewCountDesc(Pageable pageable);
    
    // 백업 및 관리용 쿼리들
    List<NewsArticle> findByIsDeletedFalseAndPublishedDateBetween(Timestamp startDate, Timestamp endDate);
    
    // 뉴스 소스 목록 조회 
    @Query("SELECT DISTINCT n.sourceIn FROM NewsArticle n WHERE n.isDeleted = false AND n.sourceIn IS NOT NULL")
    List<String> findDistinctSources();
    
    // 첫 페이지 검색 - Native Query (CAST 함수 사용)
    @Query(value = """
        SELECT * FROM news_articles n
        WHERE n.is_deleted = false
        AND (CAST(:keyword AS text) IS NULL OR CAST(:keyword AS text) = '' OR LOWER(n.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) OR LOWER(n.summary) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
        AND (CAST(:sourceIn AS text) IS NULL OR CAST(:sourceIn AS text) = '' OR n.source_in = CAST(:sourceIn AS text))
        AND (CAST(:publishDateFrom AS timestamp) IS NULL OR n.published_date >= CAST(:publishDateFrom AS timestamp))
        AND (CAST(:publishDateTo AS timestamp) IS NULL OR n.published_date <= CAST(:publishDateTo AS timestamp))
        ORDER BY 
            CASE WHEN CAST(:orderBy AS text) = 'publishDate' AND CAST(:direction AS text) = 'DESC' THEN n.published_date END DESC,
            CASE WHEN CAST(:orderBy AS text) = 'publishDate' AND CAST(:direction AS text) = 'ASC' THEN n.published_date END ASC,
            CASE WHEN CAST(:orderBy AS text) = 'viewCount' AND CAST(:direction AS text) = 'DESC' THEN n.view_count END DESC,
            CASE WHEN CAST(:orderBy AS text) = 'viewCount' AND CAST(:direction AS text) = 'ASC' THEN n.view_count END ASC,
            CASE WHEN CAST(:orderBy AS text) = 'commentCount' AND CAST(:direction AS text) = 'DESC' THEN n.comment_count END DESC,
            CASE WHEN CAST(:orderBy AS text) = 'commentCount' AND CAST(:direction AS text) = 'ASC' THEN n.comment_count END ASC,
            n.news_articles_id DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<NewsArticle> findNewsArticlesFirstPage(
        @Param("keyword") String keyword,
        @Param("interestName") String interestName,
        @Param("sourceIn") String sourceIn,
        @Param("publishDateFrom") Timestamp publishDateFrom,
        @Param("publishDateTo") Timestamp publishDateTo,
        @Param("orderBy") String orderBy,
        @Param("direction") String direction,
        @Param("limit") int limit
    );
    
    // 커서 기반 검색 - Native Query (CAST 함수 사용)
    @Query(value = """
        SELECT * FROM news_articles n
        WHERE n.is_deleted = false
        AND (CAST(:keyword AS text) IS NULL OR CAST(:keyword AS text) = '' OR LOWER(n.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) OR LOWER(n.summary) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
        AND (CAST(:sourceIn AS text) IS NULL OR CAST(:sourceIn AS text) = '' OR n.source_in = CAST(:sourceIn AS text))
        AND (CAST(:publishDateFrom AS timestamp) IS NULL OR n.published_date >= CAST(:publishDateFrom AS timestamp))
        AND (CAST(:publishDateTo AS timestamp) IS NULL OR n.published_date <= CAST(:publishDateTo AS timestamp))
        AND (
            (CAST(:orderBy AS text) = 'publishDate' AND (CAST(:cursorTimestamp AS timestamp) IS NULL OR 
                (CAST(:direction AS text) = 'DESC' AND n.published_date < CAST(:cursorTimestamp AS timestamp)) OR
                (CAST(:direction AS text) = 'ASC' AND n.published_date > CAST(:cursorTimestamp AS timestamp)))) OR
            (CAST(:orderBy AS text) = 'viewCount' AND (CAST(:cursorLong AS bigint) IS NULL OR 
                (CAST(:direction AS text) = 'DESC' AND n.view_count < CAST(:cursorLong AS bigint)) OR
                (CAST(:direction AS text) = 'ASC' AND n.view_count > CAST(:cursorLong AS bigint)))) OR
            (CAST(:orderBy AS text) = 'commentCount' AND (CAST(:cursorLong AS bigint) IS NULL OR 
                (CAST(:direction AS text) = 'DESC' AND n.comment_count < CAST(:cursorLong AS bigint)) OR
                (CAST(:direction AS text) = 'ASC' AND n.comment_count > CAST(:cursorLong AS bigint))))
        )
        ORDER BY 
            CASE WHEN CAST(:orderBy AS text) = 'publishDate' AND CAST(:direction AS text) = 'DESC' THEN n.published_date END DESC,
            CASE WHEN CAST(:orderBy AS text) = 'publishDate' AND CAST(:direction AS text) = 'ASC' THEN n.published_date END ASC,
            CASE WHEN CAST(:orderBy AS text) = 'viewCount' AND CAST(:direction AS text) = 'DESC' THEN n.view_count END DESC,
            CASE WHEN CAST(:orderBy AS text) = 'viewCount' AND CAST(:direction AS text) = 'ASC' THEN n.view_count END ASC,
            CASE WHEN CAST(:orderBy AS text) = 'commentCount' AND CAST(:direction AS text) = 'DESC' THEN n.comment_count END DESC,
            CASE WHEN CAST(:orderBy AS text) = 'commentCount' AND CAST(:direction AS text) = 'ASC' THEN n.comment_count END ASC,
            n.news_articles_id DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<NewsArticle> findNewsArticlesWithComplexSearch(
        @Param("keyword") String keyword,
        @Param("interestName") String interestName,
        @Param("sourceIn") String sourceIn,
        @Param("publishDateFrom") Timestamp publishDateFrom,
        @Param("publishDateTo") Timestamp publishDateTo,
        @Param("orderBy") String orderBy,
        @Param("direction") String direction,
        @Param("cursorTimestamp") Timestamp cursorTimestamp,
        @Param("cursorLong") Long cursorLong,
        @Param("limit") int limit
    );
    
    // 간단한 검색 메서드
    @Query("SELECT n FROM NewsArticle n WHERE n.isDeleted = false AND " +
           "(:keyword IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:sourceIn IS NULL OR n.sourceIn = :sourceIn) " +
           "ORDER BY n.publishedDate DESC")
    List<NewsArticle> searchArticles(
        @Param("keyword") String keyword,
        @Param("sourceIn") String sourceIn,
        Pageable pageable
    );
    
    // 기존 URL 목록 조회 (SpringBatch에서 사용)
    @Query(value = "SELECT source_url FROM news_articles WHERE source_url IN (:sourceUrls)", nativeQuery = true)
    List<String> findExistingSourceUrls(@Param("sourceUrls") List<String> sourceUrls);
}
