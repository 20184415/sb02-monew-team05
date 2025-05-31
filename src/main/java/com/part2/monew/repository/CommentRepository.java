package com.part2.monew.repository;

import com.part2.monew.entity.CommentsManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentsManagement, Integer> {

    @Query("""
        select c
          from CommentsManagement c
          join fetch c.user u
          join fetch c.newsArticle na
         where c.newsArticle.id = :articleId
           and (:after is null or c.createdAt < :after)
         order by c.createdAt desc
        """)
    Page<CommentsManagement> findCommentsByArticleId(
            @Param("articleId") UUID articleId,
            @Param("after") Timestamp after,
            Pageable pageable
    );
}
