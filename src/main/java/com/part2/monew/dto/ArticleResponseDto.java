package com.part2.monew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseDto {
    
    private UUID id;
    private String source;
    private String sourceUrl;
    private String title;
    private LocalDateTime publishDate;
    private String summary;
    private Long commentCount;
    private Long viewCount;
    private Boolean viewedByMe;
} 