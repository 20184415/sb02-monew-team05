package com.part2.monew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponseDto {
    
    private List<ArticleResponseDto> content;
    private String nextCursor;
    private LocalDateTime nextAfter;
    private Integer size;
    private Long totalElements;
    private Boolean hasNext;
} 