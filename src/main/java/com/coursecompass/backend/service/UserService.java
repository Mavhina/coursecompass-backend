package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.Student;
import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.repository.StudentRepository;
import com.coursecompass.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository; // inject StudentRepository
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    // Register new user
    public User registerUser(User user) {
        // Ensure role is never null
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user first
        User savedUser = userRepository.save(user);

        // If role is USER, create corresponding Student record
        if ("USER".equalsIgnoreCase(savedUser.getRole())) {
            Student student = Student.builder()
                    .user(savedUser)
                    .build(); // certificateType can be null for now
            studentRepository.save(student);
        }

        return savedUser;
    }

    // Validate login
    public boolean validateUser(String email, String rawPassword) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }
}
