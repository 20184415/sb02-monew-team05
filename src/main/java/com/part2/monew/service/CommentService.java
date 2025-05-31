package com.part2.monew.service;

import com.part2.monew.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findAll();
}
