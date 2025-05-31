package com.part2.monew.repository;

import com.part2.monew.entity.CommentsManagement;
import com.part2.monew.entity.NewsArticle;
import com.part2.monew.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@Transactional(readOnly = true)
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("게시물에 작성된 댓글을 페이지 사이즈 10~6 5개 내림차순 조회한다.")
    @Transactional
    @Test
    void findCommentsByArticleId() {
        User user = new User("test@example.com", "password123", "tester", true, Timestamp.from(Instant.now()));
        em.persist(user);

        NewsArticle newsArticle = new NewsArticle("https://example.com/foo", "제목입니다", Timestamp.from(Instant.now()), "요약입니다", 0L);
        em.persist(newsArticle);


        Instant baseTime = Instant.parse("2025-05-29T00:00:00Z");

        CommentsManagement cm1 = CommentsManagement.create(user, newsArticle, "Content1", 0, Timestamp.from(baseTime.plus(0, ChronoUnit.DAYS)));
        CommentsManagement cm2 = CommentsManagement.create(user, newsArticle, "Content2", 0, Timestamp.from(baseTime.plus(1, ChronoUnit.DAYS)));
        CommentsManagement cm3 = CommentsManagement.create(user, newsArticle, "Content3", 0, Timestamp.from(baseTime.plus(2, ChronoUnit.DAYS)));
        CommentsManagement cm4 = CommentsManagement.create(user, newsArticle, "Content4", 0, Timestamp.from(baseTime.plus(3, ChronoUnit.DAYS)));
        CommentsManagement cm5 = CommentsManagement.create(user, newsArticle, "Content5", 0, Timestamp.from(baseTime.plus(4, ChronoUnit.DAYS)));
        CommentsManagement cm6 = CommentsManagement.create(user, newsArticle, "Content6", 0, Timestamp.from(baseTime.plus(5, ChronoUnit.DAYS)));
        CommentsManagement cm7 = CommentsManagement.create(user, newsArticle, "Content7", 0, Timestamp.from(baseTime.plus(6, ChronoUnit.DAYS)));
        CommentsManagement cm8 = CommentsManagement.create(user, newsArticle, "Content8", 0, Timestamp.from(baseTime.plus(7, ChronoUnit.DAYS)));
        CommentsManagement cm9 = CommentsManagement.create(user, newsArticle, "Content9", 0, Timestamp.from(baseTime.plus(8, ChronoUnit.DAYS)));
        CommentsManagement cm10 = CommentsManagement.create(user, newsArticle, "Content10", 0, Timestamp.from(baseTime.plus(9, ChronoUnit.DAYS)));


        commentRepository.saveAll(List.of(cm1, cm2, cm3, cm4, cm5, cm6, cm7, cm8, cm9, cm10));

        Pageable pageReq = PageRequest.of(0, 5);

        // when
        Page<CommentsManagement> page = commentRepository.findCommentsByArticleId(newsArticle.getId(), null, pageReq);


        // then
        assertThat(page.getContent())
                .hasSize(5)
                .extracting(CommentsManagement::getContent)
                .containsExactly(
                        "Content10",
                        "Content9",
                        "Content8",
                        "Content7",
                        "Content6"
                );

        // 3) 다음 커서(nextAfter) 로 사용할 커서값은
        //    마지막 요소(가장 오래된 댓글)의 createdAt 을 그대로 넘기면 됩니다.
        Timestamp nextAfter = page.getContent().get(page.getContent().size() - 1).getCreatedAt();
        assertThat(nextAfter).isEqualTo(Timestamp.from(baseTime.plus(5, ChronoUnit.DAYS)));
    }

    @DisplayName("게시물에 작성된 댓글을 페이지 사이즈 5~0 내림차순 조회한다.")
    @Transactional
    @Test
    void findCommentsByArticleIdAfter() {
        User user = new User("test@example.com", "password123", "tester", true, Timestamp.from(Instant.now()));
        em.persist(user);

        NewsArticle newsArticle = new NewsArticle("https://example.com/foo", "제목입니다", Timestamp.from(Instant.now()), "요약입니다", 0L);
        em.persist(newsArticle);


        Instant baseTime = Instant.parse("2025-05-29T00:00:00Z");
        Timestamp afterTime =Timestamp.from(baseTime.plus(5, ChronoUnit.DAYS));
        Timestamp checkTime =Timestamp.from(baseTime.plus(0, ChronoUnit.DAYS));

        CommentsManagement cm1 = CommentsManagement.create(user, newsArticle, "Content1", 0, checkTime);
        CommentsManagement cm2 = CommentsManagement.create(user, newsArticle, "Content2", 0, Timestamp.from(baseTime.plus(1, ChronoUnit.DAYS)));
        CommentsManagement cm3 = CommentsManagement.create(user, newsArticle, "Content3", 0, Timestamp.from(baseTime.plus(2, ChronoUnit.DAYS)));
        CommentsManagement cm4 = CommentsManagement.create(user, newsArticle, "Content4", 0, Timestamp.from(baseTime.plus(3, ChronoUnit.DAYS)));
        CommentsManagement cm5 = CommentsManagement.create(user, newsArticle, "Content5", 0, Timestamp.from(baseTime.plus(4, ChronoUnit.DAYS)));
        CommentsManagement cm6 = CommentsManagement.create(user, newsArticle, "Content6", 0, afterTime);
        CommentsManagement cm7 = CommentsManagement.create(user, newsArticle, "Content7", 0, Timestamp.from(baseTime.plus(6, ChronoUnit.DAYS)));
        CommentsManagement cm8 = CommentsManagement.create(user, newsArticle, "Content8", 0, Timestamp.from(baseTime.plus(7, ChronoUnit.DAYS)));
        CommentsManagement cm9 = CommentsManagement.create(user, newsArticle, "Content9", 0, Timestamp.from(baseTime.plus(8, ChronoUnit.DAYS)));
        CommentsManagement cm10 = CommentsManagement.create(user, newsArticle, "Content10", 0, Timestamp.from(baseTime.plus(9, ChronoUnit.DAYS)));


        commentRepository.saveAll(List.of(cm1, cm2, cm3, cm4, cm5, cm6, cm7, cm8, cm9, cm10));

        Pageable pageReq = PageRequest.of(0, 5);

        // when
        Page<CommentsManagement> page = commentRepository.findCommentsByArticleId(newsArticle.getId(), afterTime, pageReq);


        // then
        assertThat(page.getContent())
                .hasSize(5)
                .extracting(CommentsManagement::getContent)
                .containsExactly(
                        "Content5",
                        "Content4",
                        "Content3",
                        "Content2",
                        "Content1"
                );

        // 3) 다음 커서(nextAfter) 로 사용할 커서값은
        //    마지막 요소(가장 오래된 댓글)의 createdAt 을 그대로 넘기면 됩니다.
        Timestamp nextAfter = page.getContent().get(page.getContent().size() - 1).getCreatedAt();
        assertThat(nextAfter).isEqualTo(checkTime);
    }

}