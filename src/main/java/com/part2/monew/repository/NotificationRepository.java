package com.part2.monew.repository;

import com.part2.monew.entity.Notification;
import java.sql.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdAndConfirmedFalse(UUID userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :oneWeekAgo")
    void deleteConfirmedNotificationsBefore(@Param("oneWeekAgo") Timestamp oneWeekAgo);
}
