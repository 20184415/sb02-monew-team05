package com.part2.monew.repository;

import com.part2.monew.entity.Notification;
import com.part2.monew.entity.User;
import com.part2.monew.mapper.InterestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private InterestMapper interestMapper;

    private User user;



    @Test
    void findByUserIdAndConfirmedFalse_success() {
        User user1 = new User();
        user1.setNickname("test");
        user1.setEmail("test@naver.com");
        user1.setPassword("password");
        user1.setActive(true);

        userRepository.save(user1);


        Notification n1 = new Notification(user1, "content", "COMMENT", UUID.randomUUID());
        n1.setConfirmed(false);
        notificationRepository.save(n1);

        List<Notification> result = notificationRepository.findByUserIdAndConfirmedFalse(user1.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isConfirmed()).isFalse();
    }

    @Test
    void findTop51ByUserIdAndConfirmedFalseOrderByCreatedAtDesc_success() {
        User user2 = new User();
        user2.setNickname("test");
        user2.setEmail("test@naver.com");
        user2.setPassword("password");
        user2.setActive(true);

        userRepository.save(user2);

        for (int i = 0; i < 3; i++) {
            Notification n = new Notification(user2, "content" + i, "COMMENT", UUID.randomUUID());
            n.setConfirmed(false);
            notificationRepository.save(n);
        }

        List<Notification> result = notificationRepository.findTop51ByUserIdAndConfirmedFalseOrderByCreatedAtDesc(user2.getId());
        assertThat(result).hasSize(3);
        assertThat(result).isSortedAccordingTo((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
    }



    @Test
    void countByUserIdAndConfirmedFalse_success() {
        User user3 = new User();
        user3.setNickname("test");
        user3.setEmail("test@naver.com");
        user3.setPassword("password");
        user3.setActive(true);

        userRepository.save(user3);

        Notification a1 = new Notification(user3, "content", "COMMENT", UUID.randomUUID());
        a1.setConfirmed(false);
        notificationRepository.save(a1);

        long count = notificationRepository.countByUserIdAndConfirmedFalse(user3.getId());
        assertThat(count).isEqualTo(1);
    }
}
