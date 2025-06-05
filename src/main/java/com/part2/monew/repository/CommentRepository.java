package com.part2.monew.repository;

import com.part2.monew.entity.CommentsManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentsManagement, UUID>, CommentRepositoryCustom {

    // 특정 기사의 활성 댓글 수 조회
    @Query("SELECT COUNT(c) FROM CommentsManagement c WHERE c.newsArticle.id = :articleId AND c.active = true")
    Long countActiveCommentsByArticleId(@Param("articleId") UUID articleId);

}
