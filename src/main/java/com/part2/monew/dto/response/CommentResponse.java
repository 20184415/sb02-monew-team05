package com.part2.monew.dto.response;


import com.part2.monew.entity.CommentsManagement;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
public class CommentResponse {
    private UUID id;
    private UUID articleId;
    private UUID userId;
    private String userNickname;
    private String content;
    private int likeCount;
    private Boolean likedByMe;
    private Timestamp createdAt;

    @Builder
    private CommentResponse(UUID id, UUID articleId, UUID userId, String userNickname, String content, int likeCount, boolean likedByMe, Timestamp createdAt) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
        this.likeCount = likeCount;
        this.likedByMe = likedByMe;
        this.createdAt = createdAt;
    }

    public static CommentResponse of(CommentsManagement comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .articleId(comment.getNewsArticle().getId())
                .userId(comment.getUser().getId())
                .userNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .likedByMe(comment.getCommentLikes() == null ? false : comment.getCommentLikes().size() > 1 ? true : false )
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
