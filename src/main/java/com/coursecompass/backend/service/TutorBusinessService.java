package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.TutorBusinessProfileDTO;
import com.coursecompass.backend.repository.TutorBookingRepository;
import com.coursecompass.backend.repository.TutorBusinessRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TutorBusinessService {

    private final TutorBusinessRepository businessRepo;
    private final TutorBookingRepository bookingRepo;

    public TutorBusinessService(TutorBusinessRepository businessRepo,
                                TutorBookingRepository bookingRepo) {
        this.businessRepo = businessRepo;
        this.bookingRepo = bookingRepo;
    }

    public TutorBusinessProfileDTO getProfileByBusinessId(Long businessId, User currentUserOrNull) {
        TutorBusiness b = businessRepo.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Tutor business not found: " + businessId));
        return toProfileDTO(b, currentUserOrNull);
    }

    public TutorBusinessProfileDTO getProfileByOwnerUserId(Long ownerUserId, User currentUserOrNull) {
        TutorBusiness b = businessRepo.findByOwner_Id(ownerUserId)
                .orElseThrow(() -> new RuntimeException("Tutor business not found for userId: " + ownerUserId));
        return toProfileDTO(b, currentUserOrNull);
    }

    private TutorBusinessProfileDTO toProfileDTO(TutorBusiness b, User currentUserOrNull) {

        // booking info (optional)
        boolean isBooked = false;
        TutorBookingStatus status = null;

        if (currentUserOrNull != null) {
            bookingRepo.findByStudentUser_IdAndBusiness_Id(currentUserOrNull.getId(), b.getId())
                    .ifPresent(book -> {
                        // just use holder object pattern not needed, handled below
                    });

            var bookingOpt = bookingRepo.findByStudentUser_IdAndBusiness_Id(currentUserOrNull.getId(), b.getId());
            if (bookingOpt.isPresent()) {
                isBooked = true;
                status = bookingOpt.get().getStatus();
            }
        }

        // tutors mapping
        List<TutorBusinessProfileDTO.TutorDTO> tutors = b.getTutors().stream()
                .map(t -> TutorBusinessProfileDTO.TutorDTO.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .imageUrl(t.getImageUrl())
                        .grades(t.getGrades())
                        .rating(t.getRating() == null ? 0.0 : t.getRating())
                        .reviewsCount(t.getReviewsCount() == null ? 0 : t.getReviewsCount())
                        .experienceYears(t.getExperienceYears() == null ? 0 : t.getExperienceYears())
                        .subjects(splitCsv(t.getSubjectsCsv()))
                        .build())
                .toList();

        // list mapping
        List<String> whyJoin = b.getWhyJoinItems().stream().map(TutorBusinessWhyJoin::getItem).toList();
        List<String> subjectsOffered = b.getSubjectsOffered().stream().map(TutorBusinessSubjectOffered::getSubject).toList();
        List<String> pricingIncludes = b.getPricingIncludes().stream().map(TutorBusinessPricingInclude::getItem).toList();
        List<String> gallery = b.getGalleryImages().stream().map(TutorBusinessGalleryImage::getImageUrl).toList();

        var successStories = b.getSuccessStories().stream()
                .map(s -> TutorBusinessProfileDTO.SuccessStoryDTO.builder()
                        .student(s.getStudent())
                        .subject(s.getSubject())
                        .improvement(s.getImprovement())
                        .story(s.getStory())
                        .build())
                .toList();

        var reviews = b.getReviews().stream()
                .map(r -> TutorBusinessProfileDTO.ReviewDTO.builder()
                        .student(r.getStudent())
                        .rating(r.getRating() == null ? 0 : r.getRating())
                        .comment(r.getComment())
                        .build())
                .toList();

        return TutorBusinessProfileDTO.builder()
                .id(b.getId())
                .business(TutorBusinessProfileDTO.BusinessDTO.builder()
                        .name(b.getName())
                        .headline(b.getHeadline())
                        .mode(b.getMode())
                        .location(b.getLocation())
                        .bannerImageUrl(b.getBannerImageUrl())
                        .build())
                .about(TutorBusinessProfileDTO.AboutDTO.builder()
                        .bio(b.getBio())
                        .whyJoin(whyJoin)
                        .build())
                .tutors(tutors)
                .subjectsOffered(subjectsOffered)
                .pricing(TutorBusinessProfileDTO.PricingDTO.builder()
                        .pricePerMonth(b.getPricePerMonth())
                        .currency(b.getCurrency() == null ? "ZAR" : b.getCurrency())
                        .includes(pricingIncludes)
                        .build())
                .gallery(gallery)
                .successStories(successStories)
                .reviews(reviews)
                .booking(TutorBusinessProfileDTO.BookingDTO.builder()
                        .isBookedByCurrentUser(isBooked)
                        .status(status)
                        .build())
                .build();
    }

    private List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
