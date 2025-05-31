package com.part2.monew.dto.response;

import com.part2.monew.entity.CommentsManagement;
import com.part2.monew.entity.NewsArticle;
import com.part2.monew.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


class CommentResponseTest {
    @DisplayName("CommentResponse를 생성 한다.")
    @Test
    void of(){
        // given
        Timestamp create_at = new Timestamp(System.currentTimeMillis());

        User user = new User(any(String.class), any(String.class), any(String.class), any(Boolean.class), any(Timestamp.class));
        NewsArticle newsArticle = new NewsArticle(any(String.class), any(String.class), any(Timestamp.class), any(String.class), any(Long.class));

        CommentsManagement commentsManagement = CommentsManagement.create(user, newsArticle, "content", 1, create_at);

        // when
        CommentResponse commentResponse = CommentResponse.of(commentsManagement);

        // then
        assertThat(commentResponse)
                .extracting(
                        CommentResponse::getId,
                        CommentResponse::getUserId,
                        CommentResponse::getArticledId,
                        CommentResponse::getContent,
                        CommentResponse::getLikeCount,
                        CommentResponse::getLikedByMe,
                        CommentResponse::getCreatedAt
                )
                .containsExactly(
                        commentResponse.getId(),
                        user.getId(),
                        newsArticle.getId(),
                        "content",
                        1,
                        true,
                        create_at
                );
    }
}