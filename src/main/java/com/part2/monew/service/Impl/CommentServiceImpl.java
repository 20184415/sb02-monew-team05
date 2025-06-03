package com.part2.monew.service.Impl;


import com.part2.monew.dto.request.CommentRequest;
import com.part2.monew.dto.request.CreateCommentRequest;
import com.part2.monew.dto.response.CommentLikeReponse;
import com.part2.monew.dto.response.CommentResponse;
import com.part2.monew.dto.response.CursorResponse;
import com.part2.monew.entity.CommentLike;
import com.part2.monew.entity.CommentsManagement;
import com.part2.monew.entity.NewsArticle;
import com.part2.monew.entity.User;
import com.part2.monew.repository.CommentLikeRepository;
import com.part2.monew.repository.CommentRepository;
import com.part2.monew.repository.NewsArticleRepository;
import com.part2.monew.repository.UserRepository;
import com.part2.monew.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final NewsArticleRepository articleRepository;

    @Override
    public CursorResponse findCommentsByArticleId(CommentRequest commentRequest) {

        List<CommentsManagement> commentsManagements = commentRepository.findCommentsByArticleId(commentRequest.getArticleId(), commentRequest.getAfter(), commentRequest.getLimit());

        Long totalElements = commentRepository.totalCount(commentRequest.getArticleId());

        List<CommentResponse> commentReponses = commentsManagements.stream()
                .map(CommentResponse::of)
                .collect(Collectors.toList());

        return CursorResponse.of(commentReponses, totalElements);
    }

    @Override
    @Transactional
    public CommentResponse create(CreateCommentRequest requeset) {
        User user = userRepository.findById(requeset.getUserId())
                .orElseThrow( () ->  new NoSuchElementException("user with id " + requeset.getUserId() + " not found"));


        NewsArticle article = articleRepository.findById(requeset.getArticleId())
                .orElseThrow( () ->  new NoSuchElementException("article with id " + requeset.getUserId() + " not found"));

        CommentsManagement comment = CommentsManagement.create(user, article, requeset.getContent(), 0L);

        CommentsManagement saveComment = commentRepository.saveAndFlush(comment);

        return CommentResponse.of(saveComment);

    }

    @Override
    @Transactional
    public CommentResponse update(UUID id, String content) {
        CommentsManagement commentsManagement = commentRepository.findById(id)
                .orElseThrow( () ->  new NoSuchElementException("comment with id " + id + " not found"));

        commentsManagement.update(content);

        return CommentResponse.of(commentsManagement);
    }

    @Override
    @Transactional
    public CommentLikeReponse likeComment(UUID id, UUID userId) {
        if(commentLikeRepository.existsCommentLikeByCommentsManagement_IdAndUser_Id(id, userId)){
            throw new IllegalArgumentException("좋아요를 이미 눌렀습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow( () ->  new NoSuchElementException("user with id " + userId + " not found"));

        CommentsManagement commentsManagement = commentRepository.findById(id)
                .orElseThrow( () ->  new NoSuchElementException("comment with id " + id + " not found"));


        CommentLike commentLike = CommentLike.create(user, commentsManagement);

        CommentLike saveComment = commentLikeRepository.saveAndFlush(commentLike);

        Long totalLike = commentTotalLike(commentsManagement);

        commentsManagement.updateTotalCount(totalLike);

        return CommentLikeReponse.of(commentsManagement, saveComment);
    }

    private Long commentTotalLike(CommentsManagement commentsManagement) {
        return commentLikeRepository.findAllByCommentsManagement(commentsManagement).stream().count();
    }

}
