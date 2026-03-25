package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Student;
import com.coursecompass.backend.domain.StudentAps;
import com.coursecompass.backend.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentApsRepository extends JpaRepository<StudentAps, Long> {
    List<StudentAps> findByStudent(Student student);
    Optional<StudentAps> findByStudentAndUniversity(Student student, University university);

}
