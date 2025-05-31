package com.part2.monew.dto.response;

import com.part2.monew.entity.CommentsManagement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PageResponse {
    private final List<CommentsManagement> content = new ArrayList<>();
    private String nextCursor;
    private String nextAfter;
    private int size;
    private int totalElements;
    private Boolean hasNext;
}
