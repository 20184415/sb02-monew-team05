package com.part2.monew.repository;

import com.part2.monew.entity.ActivityDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivityDetailRepository extends JpaRepository<ActivityDetail, UUID> {
    
    // 특정 사용자가 특정 기사를 조회한 기록이 있는지 확인
    @Query("SELECT COUNT(ad) > 0 FROM ActivityDetail ad WHERE ad.user.id = :userId AND ad.newsArticle.id = :articleId")
    boolean existsByUserIdAndArticleId(@Param("userId") UUID userId, @Param("articleId") UUID articleId);
    
    // 특정 사용자가 특정 기사를 조회한 기록 조회
    @Query("SELECT ad FROM ActivityDetail ad WHERE ad.user.id = :userId AND ad.newsArticle.id = :articleId")
    ActivityDetail findByUserIdAndArticleId(@Param("userId") UUID userId, @Param("articleId") UUID articleId);
} 