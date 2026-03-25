package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findFirstByFullNameContainingIgnoreCase(String name);
}
