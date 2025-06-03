package com.part2.monew.repository;

import com.part2.monew.entity.CommentLike;
import com.part2.monew.entity.CommentsManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {
    Optional<Object> findAllByCommentsManagement(CommentsManagement commentsManagement);

    boolean existsCommentLikeByCommentsManagement_IdAndUser_Id(UUID id, UUID userId);
}
