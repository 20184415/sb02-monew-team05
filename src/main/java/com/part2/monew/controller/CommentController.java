package com.part2.monew.controller;

import com.part2.monew.dto.request.CommentRequest;
import com.part2.monew.dto.request.CreateCommentRequest;
import com.part2.monew.dto.request.UpdateCommentRequest;
import com.part2.monew.dto.response.CommentLikeResponse;
import com.part2.monew.dto.response.CommentResponse;
import com.part2.monew.dto.response.CursorResponse;
import com.part2.monew.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<CursorResponse> findCommentsByArticleId(
            @Validated @ModelAttribute CommentRequest commentRequest
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(commentService.findCommentsByArticleId(commentRequest));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Validated @RequestBody CreateCommentRequest commentRequest
    ){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.create(commentRequest));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("commentId") UUID commentId,
            @Validated @RequestBody UpdateCommentRequest request
    ){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(commentId, request.getContent()));
    }

    @PostMapping("/{commentId}/comment-likes")
    public ResponseEntity<CommentLikeResponse> addCommentLikes(
            @PathVariable("commentId") UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.likeComment(commentId, userId));
    }

    @DeleteMapping("/{commentId}/comment-likes")
    public ResponseEntity<Void> removeCommentLikes(
            @PathVariable("commentId") UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId
    ){
        commentService.unlikeComment(commentId, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("commentId") UUID commentId
    ){
        commentService.deleteComment(commentId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDeleteComment(
            @PathVariable("commentId") UUID commentId
    ){
        commentService.hardDeleteComment(commentId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
