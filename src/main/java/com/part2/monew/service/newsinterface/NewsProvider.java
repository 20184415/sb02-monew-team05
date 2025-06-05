package com.part2.monew.service.newsinterface;

import com.part2.monew.config.NewsProviderProperties.ProviderConfig;
import com.part2.monew.dto.NewsArticleDto;
import java.util.List;

public interface NewsProvider {


    String getProviderKey();


    List<NewsArticleDto> fetchNews(ProviderConfig config, List<String> keywords);
}
