package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApsCalculationService {

    private final StudentSubjectResultRepository resultRepo;
    private final ApsScaleRepository scaleRepo;
    private final ApsRuleRepository ruleRepo;
    private final StudentApsRepository studentApsRepo;

    public ApsCalculationService(StudentSubjectResultRepository resultRepo,
                                 ApsScaleRepository scaleRepo,
                                 ApsRuleRepository ruleRepo, StudentApsRepository studentApsRepo) {
        this.resultRepo = resultRepo;
        this.scaleRepo = scaleRepo;
        this.ruleRepo = ruleRepo;
        this.studentApsRepo = studentApsRepo;
    }


    /**
     * Calculate the APS for a student at a university.
     */
    public int calculateAps(Student student, University university) {

        // Fetch student's subject results
        List<StudentSubjectResult> results = resultRepo.findByStudent(student);
        if (results.isEmpty()) return 0;

        // Fetch APS calculation rule for this university
        ApsRule rule = ruleRepo.findByUniversity(university)
                .orElseThrow(() -> new RuntimeException("No APS rule defined for university"));

        // Apply rule: exclude Life Orientation if required
        List<StudentSubjectResult> filteredResults = new ArrayList<>(
                results.stream()
                        .filter(r ->
                                rule.isIncludeLifeOrientation()
                                        || !r.getSubject().getName().equalsIgnoreCase("Life Orientation")
                        )
                        .toList()
        );

        if (filteredResults.isEmpty()) return 0;

        // Sort by percentage ( the best first)
        filteredResults.sort(
                Comparator.comparingInt(StudentSubjectResult::getPercentage).reversed()
        );

        // Take the BEST N subjects
        List<StudentSubjectResult> topResults = filteredResults.stream()
                .limit(rule.getMinSubjects())
                .toList();

        // Convert percentages to APS points and sum
        System.out.println("Rule includes LO? " + rule.isIncludeLifeOrientation());
        System.out.println("Filtered subjects:");
        filteredResults.forEach(r -> System.out.println(r.getSubject().getName() + " -> " + r.getPercentage()));

        int sum = topResults.stream()
                .mapToInt(r -> {
                    int pts = getApsPoints(university, r.getPercentage());
                    System.out.println(r.getSubject().getName() + " -> " + r.getPercentage() + " -> " + pts);
                    return pts;
                })
                .sum();

        // Upsert APS record
        StudentAps studentAps = studentApsRepo.findByStudentAndUniversity(student, university)
                .orElse(new StudentAps());

        studentAps.setStudent(student);
        studentAps.setUniversity(university);
        studentAps.setApsValue(sum);
        studentAps.setCalculationDate(LocalDateTime.now());

        studentApsRepo.save(studentAps);

        System.out.println("Total APS = " + sum);
        return sum;

    }

    /**
     * Convert percentage to APS points using university APS scale
     */
    private int getApsPoints(University university, int percentage) {
        return scaleRepo.findByUniversity(university).stream()
                .filter(scale -> percentage >= scale.getMinPercentage() && percentage <= scale.getMaxPercentage())
                .map(ApsScale::getApsPoints)
                .findFirst()
                .orElse(0);
    }
}
