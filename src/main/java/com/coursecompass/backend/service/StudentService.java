package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentSubjectResultRepository resultRepository;
    private final SubjectRepository subjectRepository;

    public StudentService(StudentRepository studentRepository,
                          StudentSubjectResultRepository resultRepository,
                          SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.resultRepository = resultRepository;
        this.subjectRepository = subjectRepository;
    }

    // -------------------- Student --------------------
    public Student getByUser(User user) {
        return studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found for user: " + user.getEmail()));
    }

    // -------------------- Subject Marks --------------------
    public Subject getSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
    }

    public StudentSubjectResult saveResult(StudentSubjectResult result) {
        return resultRepository.save(result);
    }

    public StudentSubjectResult getResultById(Long resultId) {
        return resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found with id: " + resultId));
    }

    public List<StudentSubjectResult> getResultsByStudent(Student student) {
        return resultRepository.findByStudent(student);
    }

    public void deleteResultsByStudent(Student student) {
        resultRepository.deleteByStudent(student);
    }
}
