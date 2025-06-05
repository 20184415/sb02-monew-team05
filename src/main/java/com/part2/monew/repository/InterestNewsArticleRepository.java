package com.part2.monew.repository;

import com.part2.monew.entity.InterestNewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterestNewsArticleRepository extends JpaRepository<InterestNewsArticle, UUID> {
    
    /**
     * 특정 뉴스 기사가 어떤 관심사들과 매핑되어 있는지 조회
     * @param newsArticleId 뉴스 기사 ID
     * @return 해당 뉴스와 매핑된 관심사 이름 목록
     */
    @Query(value = """
        SELECT i.name 
        FROM interests_news_articles ina
        JOIN interests i ON ina.interests_id = i.interest_id
        WHERE ina.news_articles_id = :newsArticleId
        """, nativeQuery = true)
    List<String> findInterestNamesByNewsArticleId(@Param("newsArticleId") UUID newsArticleId);
    
    /**
     * 특정 관심사에 매핑된 뉴스 기사 개수 조회
     * @param interestId 관심사 ID
     * @return 매핑된 뉴스 기사 개수
     */
    @Query(value = """
        SELECT COUNT(*) 
        FROM interests_news_articles ina
        WHERE ina.interests_id = :interestId
        """, nativeQuery = true)
    Long countByInterestId(@Param("interestId") UUID interestId);
    
    /**
     * 뉴스 기사와 관심사 간의 매핑이 이미 존재하는지 확인
     * @param newsArticleId 뉴스 기사 ID
     * @param interestId 관심사 ID
     * @return 매핑 존재 여부
     */
    @Query(value = """
        SELECT COUNT(*) > 0 
        FROM interests_news_articles ina
        WHERE ina.news_articles_id = :newsArticleId AND ina.interests_id = :interestId
        """, nativeQuery = true)
    boolean existsByNewsArticleIdAndInterestId(@Param("newsArticleId") UUID newsArticleId, @Param("interestId") UUID interestId);
} 