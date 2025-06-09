package com.part2.monew.repository;

import com.part2.monew.entity.Interest;
import com.part2.monew.entity.User;
import com.part2.monew.entity.UserSubscriber;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSubscriberRepository extends JpaRepository<UserSubscriber, UUID> {

  boolean existsByUser_IdAndInterest_Id(UUID userId, UUID interestId);

  Optional<UserSubscriber> findByUser_IdAndInterest_Id(UUID userId, UUID interestId);

  @Query("SELECT us.interest.id FROM UserSubscriber us WHERE us.user.id = :userId AND us.interest.id IN :interestIds")
  Set<UUID> findSubscribedInterestIdsByUserIdAndInterestIdsIn(@Param("userId") UUID userId, @Param("interestIds") List<UUID> interestIds);

  @Query("SELECT us.interest FROM UserSubscriber us WHERE us.user = :user")
  List<Interest> findInterestsByUser(@Param("user") User user);

  @Query("SELECT us FROM UserSubscriber us JOIN FETCH us.interest")
  List<UserSubscriber> findAllWithInterest();

}
