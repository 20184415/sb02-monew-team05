package com.part2.monew.repository;

import com.part2.monew.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdAndConfirmedFalse(UUID userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :oneWeekAgo")
    int deleteConfirmedNotificationsBefore(@Param("oneWeekAgo") Timestamp oneWeekAgo);

    @Modifying
    List<Notification> findTop11ByUserIdAndConfirmedFalseOrderByCreatedAtDesc(UUID userId);
    List<Notification> findTop11ByUserIdAndConfirmedFalseAndCreatedAtLessThanOrderByCreatedAtDesc(UUID userId, Timestamp cursor);
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :oneWeekAgo")

    long countByUserIdAndConfirmedFalse(UUID userId);

}
