package com.part2.monew.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponseDto<T> {
    private List<T> content;
    private String nextCursor;    // 다음 페이지 조회를 위한 주 커서
    private String nextAfter;     // 다음 페이지 조회를 위한 보조 커서 (ISO datetime 문자열)
    private int size;             // 현재 페이지의 아이템 수
    private long totalElements;   // 전체 아이템 수
    private boolean hasNext;      // 다음 페이지 존재 여부
} 