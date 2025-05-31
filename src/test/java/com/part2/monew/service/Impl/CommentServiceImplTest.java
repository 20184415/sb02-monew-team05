package com.part2.monew.service.Impl;

import com.part2.monew.entity.CommentsManagement;
import com.part2.monew.entity.NewsArticle;
import com.part2.monew.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional(readOnly = true)
class CommentServiceImplTest {


//    @DisplayName("댓글 목록을 조회한다.")
//    @Test
//    void findAll(){
//        // given
//        UUID user_id = UUID.randomUUID();
//        UUID news_articles_id = UUID.randomUUID();
//        Timestamp create_at = new Timestamp();
//
//        User user = new User(user_id, any(String.class), any(String.class), any(String.class), any(Boolean.class), any(Timestamp.class));
//        NewsArticle newsArticle = new NewsArticle(news_articles_id, any(String.class), any(String.class), any(Timestamp.class), any(String.class), any(Long.class), any(Timestamp.class));
//
//        // 기준이 될 시간 하나
//        Instant base = Instant.now();
//        // 10개의 댓글을, base 시각부터 1초씩 더한 createdAt 으로 생성
//        for (int i = 1; i <= 10; i++) {
//            Timestamp ts = Timestamp.from(base.plusSeconds(i));
//            CommentsManagement cm = CommentsManagement.create(
//                    UUID.randomUUID(),
//                    user,
//                    newsArticle,
//                    String.valueOf(i),  // content = "1", "2", ...
//                    0,
//                    ts
//            );
//            repo.save(cm);
//        }

        // when

        // then


}