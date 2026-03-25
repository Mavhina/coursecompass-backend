package com.coursecompass.backend.dto;

import com.coursecompass.backend.domain.TutorBookingStatus;
import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TutorBusinessProfileDTO {

    private Long id;

    // ── Used by student-facing TutorProfile page ──────────────────────────
    private BusinessDTO      business;
    private AboutDTO         about;
    private List<TutorDTO>   tutors;         // student-side tutor cards
    private List<String>     subjectsOffered;
    private PricingDTO       pricing;
    private List<String>     gallery;
    private List<SuccessStoryDTO> successStories;
    private List<ReviewDTO>  reviews;
    private BookingDTO       booking;

    // ── Used by tutor-facing BusinessProfile edit page ────────────────────
    // (flat fields for the edit form — populated by TutorBusinessProfileService)
    private String  name;
    private String  headline;
    private String  mode;
    private String  location;
    private String  bannerImageUrl;
    private String  bio;
    private Integer pricePerMonth;
    private String  currency;
    private List<String>           whyJoinItems;
    private List<String>           pricingIncludes;
    private List<String>           galleryImages;      // flat URL list for edit form
    private List<BusinessTutorDTO> businessTutors;     // tutor cards for edit form

    // ═══════════════════════════════════════════════════════════════════════
    // Nested DTOs — student-facing (used by TutorBusinessService)
    // ═══════════════════════════════════════════════════════════════════════

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BusinessDTO {
        private String name;
        private String headline;
        private String mode;
        private String location;
        private String bannerImageUrl;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AboutDTO {
        private String       bio;
        private List<String> whyJoin;
    }

    /** Student-facing tutor card — used by TutorBusinessService */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TutorDTO {
        private Long    id;
        private String  name;
        private String  imageUrl;
        private String  grades;
        private Double  rating;
        private Integer reviewsCount;
        private Integer experienceYears;
        private List<String> subjects;   // split from CSV
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PricingDTO {
        private Integer      pricePerMonth;
        private String       currency;
        private List<String> includes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookingDTO {
        private boolean           isBookedByCurrentUser;
        private TutorBookingStatus status;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Nested DTOs — shared by both sides
    // ═══════════════════════════════════════════════════════════════════════

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReviewDTO {
        private Long    id;
        private String  student;
        private Integer rating;
        private String  comment;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SuccessStoryDTO {
        private Long   id;
        private String student;
        private String subject;
        private String improvement;
        private String story;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Nested DTOs — tutor-facing edit form only
    // ═══════════════════════════════════════════════════════════════════════

    /** Tutor-facing edit card — raw CSV subjects, used by TutorBusinessProfileService */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BusinessTutorDTO {
        private Long    id;
        private String  name;
        private String  imageUrl;
        private String  subjectsCsv;   // comma-separated, raw
        private String  grades;
        private Double  rating;
        private Integer reviewsCount;
        private Integer experienceYears;
    }
}