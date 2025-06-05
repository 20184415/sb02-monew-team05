package com.part2.monew.mapper;

import com.part2.monew.dto.NewsArticleResponseDto;
import com.part2.monew.entity.NewsArticle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // 스프링 빈으로 등록
public interface NewsArticleMapper {

    @Mapping(target = "viewedByMe", source = "viewedByMeValue")
    @Mapping(target = "source", source = "newsArticle.sourceIn")
    @Mapping(target = "publishDate", source = "newsArticle.publishedDate")
    @Mapping(target = "commentCount", source = "actualCommentCount")
    NewsArticleResponseDto toDto(NewsArticle newsArticle, Boolean viewedByMeValue, Long actualCommentCount);

    // 기존 메서드는 호환성을 위해 유지 (기본 commentCount 사용)
    @Mapping(target = "viewedByMe", source = "viewedByMeValue")
    @Mapping(target = "source", source = "newsArticle.sourceIn")
    @Mapping(target = "publishDate", source = "newsArticle.publishedDate")
    NewsArticleResponseDto toDto(NewsArticle newsArticle, Boolean viewedByMeValue);

} 