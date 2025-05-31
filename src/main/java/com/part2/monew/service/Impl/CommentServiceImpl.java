package com.part2.monew.service.Impl;

import com.part2.monew.dto.request.CommentRequest;
import com.part2.monew.dto.response.CommentResponse;
import com.part2.monew.dto.response.PageResponse;
import com.part2.monew.entity.CommentsManagement;
import com.part2.monew.repository.CommentRepository;
import com.part2.monew.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;


//@Service
//@RequiredArgsConstructor
//@Transactional
//public class CommentServiceImpl implements CommentService {
//
//    private final CommentRepository commentRepository;
//
//    @Override
//    public PageResponse<CommentResponse> findByComment(CommentRequest commentRequest) {
//
//        Page<CommentsManagement> commentsManagements = commentRepository.findCommentsByArticleId(commentRequest.getArticleId(), commentRequest.getAfter());
//
//        List<CommentResponse> content =  commentsManagements.stream().map(CommentResponse::of).toList();
//
//        return null;
//    }
//
//}
