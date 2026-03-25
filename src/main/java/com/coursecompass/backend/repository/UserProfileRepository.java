package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
}
