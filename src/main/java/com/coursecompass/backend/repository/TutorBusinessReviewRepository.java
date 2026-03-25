package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.TutorBusinessReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorBusinessReviewRepository extends JpaRepository<TutorBusinessReview, Long> {

    // Average rating for all reviews linked to this tutor's businesses
    @Query("SELECT AVG(r.rating) FROM TutorBusinessReview r WHERE r.business.owner.id = :tutorId")
    Double avgRatingByTutorId(@Param("tutorId") Long tutorId);

    // Total number of reviews for this tutor
    @Query("SELECT COUNT(r) FROM TutorBusinessReview r WHERE r.business.owner.id = :tutorId")
    Long countReviewsByTutorId(@Param("tutorId") Long tutorId);
}