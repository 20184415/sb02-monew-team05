package com.part2.monew.repository;

import com.part2.monew.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmail(String email);
    Optional<User> findByIdAndActiveTrue(UUID id);
    Optional<User> findByEmailAndActiveTrue(String email);
}
