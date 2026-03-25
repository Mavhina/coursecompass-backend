package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public class BursarySpecifications {

    private static Specification<Bursary> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<Bursary> search(String q) {
        if (q == null || q.isBlank()) return alwaysTrue();

        String like = "%" + q.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("provider")), like),
                cb.like(cb.lower(root.get("summary")), like)
        );
    }

    public static Specification<Bursary> hasStatus(BursaryStatus status) {
        if (status == null) return alwaysTrue();
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Bursary> hasFundingType(FundingType fundingType) {
        if (fundingType == null) return alwaysTrue();
        return (root, query, cb) -> cb.equal(root.get("fundingType"), fundingType);
    }

    public static Specification<Bursary> hasStudyLevel(StudyLevel level) {
        if (level == null) return alwaysTrue();
        return (root, query, cb) -> {
            root.join("studyLevels", JoinType.LEFT);
            query.distinct(true);
            return cb.equal(root.join("studyLevels").get("level"), level);
        };
    }

    public static Specification<Bursary> hasInstitutionType(InstitutionType type) {
        if (type == null) return alwaysTrue();
        return (root, query, cb) -> {
            root.join("institutionTypes", JoinType.LEFT);
            query.distinct(true);
            return cb.equal(root.join("institutionTypes").get("institutionType"), type);
        };
    }

    public static Specification<Bursary> hasField(FieldCategory field) {
        if (field == null) return alwaysTrue();
        return (root, query, cb) -> {
            root.join("fields", JoinType.LEFT);
            query.distinct(true);
            return cb.equal(root.join("fields").get("field"), field);
        };
    }

    public static Specification<Bursary> apsMinAtMost(Integer apsMin) {
        if (apsMin == null) return alwaysTrue();
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("minAps")),
                cb.lessThanOrEqualTo(root.get("minAps"), apsMin)
        );
    }

    public static Specification<Bursary> incomeMaxAtLeast(Long incomeMax) {
        if (incomeMax == null) return alwaysTrue();
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("incomeMax")),
                cb.greaterThanOrEqualTo(root.get("incomeMax"), incomeMax)
        );
    }
}
