package com.part2.monew.repository;

import com.part2.monew.entity.NewsArticle;
import java.sql.Timestamp;
import java.util.List;

public interface NewsArticleRepositoryCustom {
    List<NewsArticle> findArticlesSortedByCommentCount(
        String keyword,
        String sourceIn,
        Timestamp publishDateFrom,
        Timestamp publishDateTo,
        String direction,
        String cursor,
        int limit
    );
    
    List<NewsArticle> findArticlesWithFiltersAndSorting(
        String keyword,
        String sourceIn,
        Timestamp publishDateFrom,
        Timestamp publishDateTo,
        String orderBy,
        String direction,
        String cursor,
        int limit
    );
    
    List<NewsArticle> findArticlesSortedByViewCount(
        String keyword,
        String sourceIn,
        Timestamp publishDateFrom,
        Timestamp publishDateTo,
        String direction,
        String cursor,
        int limit
    );
    
    List<NewsArticle> findArticlesSortedByPublishDate(
        String keyword,
        String sourceIn,
        Timestamp publishDateFrom,
        Timestamp publishDateTo,
        String direction,
        String cursor,
        int limit
    );
    
    List<String> findExistingSourceUrls(List<String> sourceUrls);
} 