package com.part2.monew.dto.request;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
public class CommentRequest {
    private UUID articleId;

    private String orderBy;

    private String direction;

    private String cursor;

    private Timestamp after;

    private Integer limit;

    private UUID requestUserId;
}
