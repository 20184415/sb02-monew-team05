package com.part2.monew.service.implement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.part2.monew.dto.FilterDto;
import com.part2.monew.dto.NewsArticleResponseDto;
import com.part2.monew.dto.PaginatedResponseDto;
import com.part2.monew.dto.RequestCursorDto;
import com.part2.monew.dto.RestoreResultDto;
import com.part2.monew.dto.response.ArticleListResponseDto;
import com.part2.monew.entity.NewsArticle;
import com.part2.monew.global.exception.article.ArticleNotFoundException;
import com.part2.monew.global.exception.user.UserNotFoundException;
import com.part2.monew.global.exception.article.ArticleSearchFailedException;
import com.part2.monew.global.exception.article.ArticleDeleteFailedException;
import com.part2.monew.global.exception.article.ArticleBackupFailedException;
import com.part2.monew.global.exception.article.ArticleRestoreFailedException;
import com.part2.monew.mapper.NewsArticleMapper;
import com.part2.monew.repository.CommentRepository;
import com.part2.monew.repository.NewsArticleRepository;
import com.part2.monew.repository.ActivityDetailRepository;
import com.part2.monew.repository.UserRepository;
import com.part2.monew.entity.ActivityDetail;
import com.part2.monew.entity.User;
import com.part2.monew.service.NewsBackupS3Manager;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsArticleService {

    private static final Logger logger = LoggerFactory.getLogger(NewsArticleService.class);

    @Getter
    private final NewsArticleRepository newsArticleRepository;
    private final NewsArticleMapper newsArticleMapper;
    private final NewsBackupS3Manager newsBackupS3Manager;
    private final ActivityDetailRepository activityDetailRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;

    public NewsArticleService(NewsArticleRepository newsArticleRepository,
        NewsArticleMapper newsArticleMapper, NewsBackupS3Manager newsBackupS3Manager,
        ActivityDetailRepository activityDetailRepository, UserRepository userRepository,
        CommentRepository commentRepository) {
        this.newsArticleRepository = newsArticleRepository;
        this.newsArticleMapper = newsArticleMapper;
        this.newsBackupS3Manager = newsBackupS3Manager;
        this.activityDetailRepository = activityDetailRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<NewsArticleResponseDto> getArticles(FilterDto filterDto,
        RequestCursorDto cursorDto, String userId) {
        logger.info("뉴스 기사 조회 요청 - 사용자 ID: {}, 필터: {}, 커서: {}", userId, filterDto, cursorDto);

        int fetchLimit = cursorDto.limit() + 1; // hasNext 확인용 +1
        List<NewsArticle> articles;
        long totalElements = 0;

        // 커서 값 준비
        Timestamp cursorTimestamp = null;
        Long cursorLong = null;

        if (cursorDto.cursor() != null && !cursorDto.cursor().isEmpty()) {
            // orderBy에 따라 커서 값 타입 결정
            if ("publishDate".equals(cursorDto.orderBy())) {
                cursorTimestamp = cursorDto.after(); // Timestamp 타입
            } else if ("viewCount".equals(cursorDto.orderBy())) {
                cursorLong = cursorDto.cursorViewCount(); // Long 타입
            } else if ("commentCount".equals(cursorDto.orderBy())) {
                try {
                    cursorLong = Long.parseLong(cursorDto.cursor()); // String을 Long으로 변환
                } catch (NumberFormatException e) {
                    logger.warn("commentCount 커서 값 파싱 실패: {}", cursorDto.cursor());
                }
            }
        }

        // orderBy 값을 Repository 쿼리에 맞게 변환
        String repositoryOrderBy = cursorDto.orderBy();
        if ("publishDate".equals(repositoryOrderBy)) {
            repositoryOrderBy = "publishedDate";
        }

        try {
            Timestamp startTimestamp = filterDto.publishDateFrom();
            Timestamp endTimestamp = filterDto.publishDateTo();

            // 커서 값이 있는지 확인 (첫 페이지인지 다음 페이지인지 판단)
            boolean isFirstPage = (cursorDto.cursor() == null || cursorDto.cursor().isEmpty());

            if (isFirstPage) {
                // 첫 페이지 조회
                logger.info("첫 페이지 검색 시작 - keyword: {}, source: {}, orderBy: {}",
                    filterDto.keyword(), getFirstSource(filterDto.sourceIn()), cursorDto.orderBy());

                Pageable pageable = PageRequest.of(0, fetchLimit);
                articles = newsArticleRepository.findNewsArticlesFirstPage(filterDto.keyword(),
                    null, // interestName - 현재 사용하지 않음
                    getFirstSource(filterDto.sourceIn()), startTimestamp, endTimestamp,
                    repositoryOrderBy, cursorDto.direction(), fetchLimit);

                logger.info("첫 페이지 검색 완료 - 결과: {}개", articles.size());
            } else {
                // 커서 기반 다음 페이지 조회
                logger.info("커서 기반 검색 시작 - keyword: {}, source: {}, orderBy: {}, cursor: {}",
                    filterDto.keyword(), getFirstSource(filterDto.sourceIn()), cursorDto.orderBy(),
                    cursorDto.cursor());

                articles = newsArticleRepository.findNewsArticlesWithComplexSearch(
                    filterDto.keyword(), null, // interestName - 현재 사용하지 않음
                    getFirstSource(filterDto.sourceIn()), startTimestamp, endTimestamp,
                    repositoryOrderBy, cursorDto.direction(), cursorTimestamp, cursorLong,
                    fetchLimit);

                logger.info("커서 기반 검색 완료 - 결과: {}개", articles.size());
            }

        } catch (Exception e) {
            logger.error("뉴스 기사 검색 중 오류 발생", e);
            throw new ArticleSearchFailedException();
        }

        // hasNext 여부 확인 및 결과 조정
        boolean hasNext = articles.size() > cursorDto.limit();
        List<NewsArticle> limitedArticles =
            hasNext ? articles.subList(0, cursorDto.limit()) : articles;

        // 다음 커서 값 생성
        String nextCursor = null;
        Timestamp nextAfter = null;
        Long nextCursorViewCount = null;

        if (hasNext && !limitedArticles.isEmpty()) {
            NewsArticle lastArticle = limitedArticles.get(limitedArticles.size() - 1);
            nextCursor = lastArticle.getId().toString();
            nextAfter = lastArticle.getPublishedDate();
            nextCursorViewCount = lastArticle.getViewCount();
        }

        // 응답 DTO 변환 (실제 댓글 수 포함)
        Map<UUID, Boolean> viewedStatusMap = Collections.emptyMap();
        List<NewsArticleResponseDto> responseDtos = limitedArticles.stream().map(article -> {
            // 실제 댓글 수 계산
            Long actualCommentCount = commentRepository.countActiveCommentsByArticleId(
                article.getId());
            return newsArticleMapper.toDto(article,
                viewedStatusMap.getOrDefault(article.getId(), false), actualCommentCount);
        }).collect(Collectors.toList());

        return PaginatedResponseDto.<NewsArticleResponseDto>builder().content(responseDtos)
            .nextCursor(nextCursor).nextAfter(nextAfter).nextCursorViewCount(nextCursorViewCount)
            .size(limitedArticles.size()).totalElements(limitedArticles.size())
            .hasNext(hasNext).build();
    }

    public List<String> getNewsSources() {
        try {
            // 실제 데이터베이스에서 distinct source 값들을 조회
            List<String> sources = newsArticleRepository.findDistinctSources();

            // 빈 리스트이거나 null인 경우 기본값 제공
            if (sources == null || sources.isEmpty()) {
                sources = Arrays.asList("chosun", "hankyung", "yonhapnewstv", "NAVER");
                logger.warn("DB에서 뉴스 소스를 찾을 수 없어 기본값 사용: {}", sources);
            } else {
                logger.info("DB에서 뉴스 소스 목록 조회: {}", sources);
            }

            return sources;
        } catch (Exception e) {
            logger.error("뉴스 소스 조회 중 오류 발생", e);
            // 오류 발생시 기본값 반환
            List<String> defaultSources = Arrays.asList("chosun", "hankyung", "yonhapnewstv",
                "NAVER");
            return defaultSources;
        }
    }

    public NewsArticle getArticleById(UUID articleId) {
        return newsArticleRepository.findActiveById(articleId)
            .orElseThrow(() -> new ArticleNotFoundException());
    }


    @Transactional
    public void softDeleteArticle(UUID articleId) {
        NewsArticle article = newsArticleRepository.findActiveById(articleId)
            .orElseThrow(() -> new ArticleNotFoundException());

        try {
            article.softDelete();
            newsArticleRepository.save(article);
            logger.info("뉴스 기사 논리 삭제 완료: {}", articleId);
        } catch (Exception e) {
            logger.error("뉴스 기사 논리 삭제 실패: {}", articleId, e);
            throw new ArticleDeleteFailedException();
        }
    }

    @Transactional
    public void hardDeleteArticle(UUID articleId) {
        NewsArticle article = newsArticleRepository.findById(articleId)
            .orElseThrow(() -> new ArticleNotFoundException());

        try {
            newsArticleRepository.delete(article);
            logger.info("뉴스 기사 물리 삭제 완료: {}", articleId);
        } catch (Exception e) {
            logger.error("뉴스 기사 물리 삭제 실패: {}", articleId, e);
            throw new ArticleDeleteFailedException();
        }
    }


    public boolean existsActiveArticle(UUID articleId) {
        return newsArticleRepository.findActiveById(articleId).isPresent();
    }


    @Transactional
    public List<RestoreResultDto> restoreArticles(Timestamp from, Timestamp to) {
        logger.info("뉴스 기사 복구 요청: {} ~ {}", from, to);

        List<RestoreResultDto> restoreResults = new ArrayList<>();
        List<UUID> allRestoredIds = new ArrayList<>();

        LocalDate fromDate = from.toLocalDateTime().toLocalDate();
        LocalDate toDate = to.toLocalDateTime().toLocalDate();

        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            String s3Key = newsBackupS3Manager.getBackupFileKey(date);
            List<UUID> dailyRestoredIds = new ArrayList<>();

            try (InputStream backupStream = newsBackupS3Manager.downloadNewsBackup(s3Key)) {
                if (backupStream == null) {
                    logger.warn("{} 날짜의 백업 파일이 S3에 없습니다. Key: {}", date, s3Key);
                    continue;
                }

                List<NewsArticle> articlesFromBackup = objectMapper.readValue(backupStream,
                    new TypeReference<List<NewsArticle>>() {
                    });

                if (articlesFromBackup != null && !articlesFromBackup.isEmpty()) {
                    logger.info("{} 날짜의 백업에서 {}개 기사 로드됨", date, articlesFromBackup.size());

                    for (NewsArticle article : articlesFromBackup) {
                        // 날짜 범위 필터링
                        if (article.getPublishedDate() != null) {
                            Timestamp articleTimestamp = article.getPublishedDate();
                            if (articleTimestamp.before(from) || articleTimestamp.after(to)) {
                                continue;
                            }
                        }

                        // DB에 이미 존재하는지 확인 (sourceUrl 기준)
                        if (!newsArticleRepository.existsBySourceUrl(article.getSourceUrl())) {
                            NewsArticle newArticle = NewsArticle.builder()
                                .sourceIn(article.getSourceIn()).sourceUrl(article.getSourceUrl())
                                .title(article.getTitle()).publishedDate(article.getPublishedDate())
                                .summary(article.getSummary()).viewCount(article.getViewCount())
                                .commentCount(article.getCommentCount())
                                .isDeleted(false) // 복구된 기사는 활성 상태
                                .build();

                            NewsArticle savedArticle = newsArticleRepository.save(newArticle);
                            dailyRestoredIds.add(savedArticle.getId());
                            allRestoredIds.add(savedArticle.getId());
                        }
                    }
                }

                // 일일 복구 결과 추가 (복구된 기사가 있는 경우에만)
                if (!dailyRestoredIds.isEmpty()) {
                    RestoreResultDto dailyResult = RestoreResultDto.builder()
                        .restoreDate(new Timestamp(System.currentTimeMillis()))
                        .restoredArticleIds(dailyRestoredIds)
                        .restoredArticleCount((long) dailyRestoredIds.size()).build();
                    restoreResults.add(dailyResult);
                }

            } catch (Exception e) {
                logger.error("{} 날짜의 백업 복구 중 오류 발생", date, e);
            }
        }

        logger.info("뉴스 복구 완료. 총 {}개 기사 복구됨", allRestoredIds.size());
        return restoreResults;
    }

    @Transactional
    public void restoreDataByDateRange(String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);

        logger.info("데이터 복구 요청: {} ~ {}", fromDate, toDate);

        List<NewsArticle> restoredArticles = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            String s3Key = newsBackupS3Manager.getBackupFileKey(date);
            try (InputStream backupStream = newsBackupS3Manager.downloadNewsBackup(s3Key)) {
                if (backupStream == null) {
                    logger.warn("{} 날짜의 백업 파일이 S3에 없습니다. Key: {}", date, s3Key);
                    continue;
                }

                List<NewsArticle> articlesFromBackup = objectMapper.readValue(backupStream,
                    new TypeReference<List<NewsArticle>>() {
                    });

                if (articlesFromBackup != null && !articlesFromBackup.isEmpty()) {
                    logger.info("{} 날짜의 백업에서 {}개 기사 로드됨. Key: {}", date, articlesFromBackup.size(),
                        s3Key);
                    for (NewsArticle article : articlesFromBackup) {
                        // DB에 이미 존재하는지 확인
                        if (!newsArticleRepository.existsBySourceUrl(article.getSourceUrl())) {

                            NewsArticle newArticle = NewsArticle.builder()
                                .sourceIn(article.getSourceIn()).sourceUrl(article.getSourceUrl())
                                .title(article.getTitle()).publishedDate(article.getPublishedDate())
                                .summary(article.getSummary()).viewCount(article.getViewCount())
                                .commentCount(article.getCommentCount())
                                .isDeleted(article.getIsDeleted()).build();
                            newsArticleRepository.save(newArticle);
                            restoredArticles.add(newArticle);
                            logger.debug("복구된 기사 저장: {}", newArticle.getSourceUrl());
                        } else {
                            logger.debug("이미 존재하는 기사 (복구 건너뜀): {}", article.getSourceUrl());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("{} 날짜의 데이터 복구 중 오류 발생. Key: {}. Error: {}", date, s3Key,
                    e.getMessage(), e);
            }
        }

        logger.info("데이터 복구 완료: 총 {}개 기사 복구됨. 기간: {} ~ {}", restoredArticles.size(), fromDate,
            toDate);
    }


    public void backupDataByDate(LocalDate date) {
        logger.info("데이터 백업 시작: {}", date);

        LocalDateTime startOfDayLocalDateTime = date.atStartOfDay();
        LocalDateTime endOfDayLocalDateTime = date.atTime(LocalTime.MAX);

        Timestamp startOfDayTimestamp = Timestamp.valueOf(startOfDayLocalDateTime);
        Timestamp endOfDayTimestamp = Timestamp.valueOf(endOfDayLocalDateTime);

        List<NewsArticle> articlesToBackup = newsArticleRepository.findByIsDeletedFalseAndPublishedDateBetween(
            startOfDayTimestamp, endOfDayTimestamp);

        if (articlesToBackup.isEmpty()) {
            logger.info("{} 날짜에 백업할 뉴스 기사가 없습니다.", date);
            return;
        }

        try {
            byte[] jsonData = objectMapper.writeValueAsBytes(articlesToBackup);
            String s3Key = newsBackupS3Manager.getBackupFileKey(date);
            newsBackupS3Manager.uploadNewsBackup(jsonData, s3Key);
            logger.info("데이터 백업 완료: {} 날짜의 {}개 기사. S3 Key: {}", date, articlesToBackup.size(),
                s3Key);
        } catch (Exception e) {
            logger.error("{} 날짜의 데이터 백업 중 오류 발생: {}", date, e.getMessage(), e);
            throw new RuntimeException(date + " 날짜 데이터 백업 실패", e);
        }
    }

    @Transactional
    public void incrementViewCount(UUID articleId, UUID userId) {
        NewsArticle article = newsArticleRepository.findActiveById(articleId)
            .orElseThrow(() -> new ArticleNotFoundException());

        User user = userRepository.findByIdAndActiveTrue(userId)
            .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // 이미 조회한 기록이 있는지 확인
        boolean alreadyViewed = activityDetailRepository.existsByUserIdAndArticleId(userId,
            articleId);

        if (!alreadyViewed) {
            // 처음 보는 기사인 경우에만 조회수 증가
            article.incrementViewCount();
            newsArticleRepository.save(article);

            // 활동 기록 저장
            ActivityDetail activityDetail = ActivityDetail.builder().user(user).newsArticle(article)
                .viewedAt(new Timestamp(System.currentTimeMillis())).build();

            activityDetailRepository.save(activityDetail);

            logger.info("뉴스 기사 조회수 증가 및 활동 기록 저장: {} (현재 조회수: {})", articleId,
                article.getViewCount());
        } else {
            logger.info("이미 조회한 기사이므로 조회수 증가 안함: {} (사용자: {})", articleId, userId);
        }
    }

    private boolean isSimpleKeywordSearch(FilterDto filterDto) {
        return filterDto.keyword() != null && filterDto.interestId() == null && (
            filterDto.sourceIn() == null || filterDto.sourceIn().isEmpty())
            && filterDto.publishDateFrom() == null && filterDto.publishDateTo() == null;
    }


    private String getFirstSource(List<String> sources) {
        return (sources != null && !sources.isEmpty()) ? sources.get(0) : null;
    }

    private UUID parseCursorId(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }
        return UUID.fromString(cursor);
    }

    private List<NewsArticle> limitResults(List<NewsArticle> articles, int limit) {
        if (articles.isEmpty()) {
            return articles;
        }

        int actualLimit = Math.min(limit, articles.size());
        return articles.subList(0, actualLimit);
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 정각
    public void executeDailyNewsBackupBatch() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            backupDataByDate(yesterday);
        } catch (Exception e) {
            logger.error("일일 뉴스 백업 배치 실행 중 오류 발생: {}", e.getMessage(), e);

        }
    }

    public PaginatedResponseDto<NewsArticleResponseDto> getArticlesByParams(String keyword,
        UUID interestId, List<String> sourceIn, Timestamp publishDateFrom, Timestamp publishDateTo,
        String orderBy, String direction, String cursor, Timestamp after, int limit,
        String userId) {

        FilterDto filterDto = new FilterDto(keyword, interestId, sourceIn, publishDateFrom,
            publishDateTo);
        RequestCursorDto cursorDto = new RequestCursorDto(orderBy, direction, cursor, after, null,
            limit);

        return getArticles(filterDto, cursorDto, userId);
    }

    public PaginatedResponseDto<NewsArticleResponseDto> getRecentArticles(int limit,
        String userId) {
        Pageable pageable = PageRequest.of(0, limit);
        List<NewsArticle> articles = newsArticleRepository.findRecentNews(pageable);

        // TODO: viewedByMe 로직 구현 필요
        Map<UUID, Boolean> viewedStatusMap = Collections.emptyMap();

        List<NewsArticleResponseDto> responseDtos = articles.stream().map(article -> {
            // 실제 댓글 수 계산
            Long actualCommentCount = commentRepository.countActiveCommentsByArticleId(
                article.getId());
            return newsArticleMapper.toDto(article,
                viewedStatusMap.getOrDefault(article.getId(), false), actualCommentCount);
        }).collect(Collectors.toList());

        return PaginatedResponseDto.<NewsArticleResponseDto>builder().content(responseDtos)
            .nextCursor(null).nextAfter(null).nextCursorViewCount(null).size(articles.size())
            .totalElements(articles.size()).hasNext(false).build();
    }

    public PaginatedResponseDto<NewsArticleResponseDto> searchArticlesByKeyword(String keyword,
        int limit, String userId) {
        Pageable pageable = PageRequest.of(0, limit);
        List<NewsArticle> articles = newsArticleRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(
            keyword, pageable);

        logger.info("키워드 '{}' 검색 결과: {}개 기사", keyword, articles.size());

        // TODO: viewedByMe 로직 구현 필요
        Map<UUID, Boolean> viewedStatusMap = Collections.emptyMap();

        List<NewsArticleResponseDto> responseDtos = articles.stream().map(article -> {
            // 실제 댓글 수 계산
            Long actualCommentCount = commentRepository.countActiveCommentsByArticleId(
                article.getId());
            return newsArticleMapper.toDto(article,
                viewedStatusMap.getOrDefault(article.getId(), false), actualCommentCount);
        }).collect(Collectors.toList());

        return PaginatedResponseDto.<NewsArticleResponseDto>builder().content(responseDtos)
            .nextCursor(null).nextAfter(null).nextCursorViewCount(null).size(articles.size())
            .totalElements(articles.size()).hasNext(false).build();
    }

    public PaginatedResponseDto<NewsArticleResponseDto> searchArticlesByKeywordAdvanced(
        String keyword, String orderBy, String direction, int limit, String userId) {

        Pageable pageable = PageRequest.of(0, limit);

        // 기본값 설정
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "publishDate";
        }
        if (direction == null || direction.trim().isEmpty()) {
            direction = "DESC";
        }

        // 빈 문자열을 null로 변환 (쿼리에서 조건 무시하도록)
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        List<NewsArticle> articles = newsArticleRepository.searchArticlesByKeywordAdvanced(keyword,
            orderBy, direction, pageable);

        logger.info("향상된 키워드 '{}' 검색 결과: {}개 기사 (정렬: {} {})", keyword, articles.size(), orderBy,
            direction);

        Map<UUID, Boolean> viewedStatusMap = Collections.emptyMap();

        List<NewsArticleResponseDto> responseDtos = articles.stream().map(article -> {
            // 실제 댓글 수 계산
            Long actualCommentCount = commentRepository.countActiveCommentsByArticleId(
                article.getId());
            return newsArticleMapper.toDto(article,
                viewedStatusMap.getOrDefault(article.getId(), false), actualCommentCount);
        }).collect(Collectors.toList());

        return PaginatedResponseDto.<NewsArticleResponseDto>builder().content(responseDtos)
            .nextCursor(null).nextAfter(null).nextCursorViewCount(null).size(articles.size())
            .totalElements(articles.size()).hasNext(false).build();
    }

    public ArticleListResponseDto<NewsArticleResponseDto> getArticlesForSwagger(FilterDto filterDto,
        RequestCursorDto cursorDto, String userId) {
        logger.info("뉴스 기사 조회 요청 (스웨거용) - 사용자 ID: {}, 필터: {}, 커서: {}", userId, filterDto,
            cursorDto);

        PaginatedResponseDto<NewsArticleResponseDto> result = getArticles(filterDto, cursorDto,
            userId);

        String nextAfterStr = null;
        if (result.getNextAfter() != null) {
            nextAfterStr = result.getNextAfter().toInstant().toString();
        }

        // 새로운 DTO로 변환
        return ArticleListResponseDto.<NewsArticleResponseDto>builder().content(result.getContent())
            .nextCursor(result.getNextCursor()).nextAfter(nextAfterStr).size(result.getSize())
            .totalElements(result.getTotalElements()).hasNext(result.isHasNext()).build();
    }
}
