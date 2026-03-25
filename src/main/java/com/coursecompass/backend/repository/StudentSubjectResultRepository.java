package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Student;
import com.coursecompass.backend.domain.StudentSubjectResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSubjectResultRepository extends JpaRepository<StudentSubjectResult, Long> {
    List<StudentSubjectResult> findByStudent(Student student);
    void deleteByStudent(Student student);
}
