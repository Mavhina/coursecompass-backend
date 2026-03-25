package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.TutorBusinessProfileDTO;
import com.coursecompass.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TutorBusinessProfileService {

    private final TutorBusinessRepository    businessRepo;
    private final UserRepository             userRepo;

    // ── READ ──────────────────────────────────────────────────────────────

    public TutorBusinessProfileDTO getOrEmpty(Long tutorUserId) {
        Optional<TutorBusiness> opt = businessRepo.findByOwner_Id(tutorUserId);
        return opt.map(this::toDTO).orElseGet(TutorBusinessProfileDTO::new);
    }

    // ── SAVE (create or full replace) ─────────────────────────────────────

    @Transactional
    public TutorBusinessProfileDTO save(Long tutorUserId, TutorBusinessProfileDTO dto) {
        User owner = userRepo.findById(tutorUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TutorBusiness biz = businessRepo.findByOwner_Id(tutorUserId)
                .orElseGet(() -> TutorBusiness.builder().owner(owner).build());

        // Basic fields
        biz.setName(dto.getName());
        biz.setHeadline(dto.getHeadline());
        biz.setMode(dto.getMode());
        biz.setLocation(dto.getLocation());
        biz.setBannerImageUrl(dto.getBannerImageUrl());
        biz.setBio(dto.getBio());
        biz.setPricePerMonth(dto.getPricePerMonth());
        biz.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "ZAR");

        // ── Subjects ──────────────────────────────────────────────────────
        biz.getSubjectsOffered().clear();
        if (dto.getSubjectsOffered() != null) {
            dto.getSubjectsOffered().forEach(s -> {
                TutorBusinessSubjectOffered e = new TutorBusinessSubjectOffered();
                e.setBusiness(biz);
                e.setSubject(s);
                biz.getSubjectsOffered().add(e);
            });
        }

        // ── Why Join ──────────────────────────────────────────────────────
        biz.getWhyJoinItems().clear();
        if (dto.getWhyJoinItems() != null) {
            dto.getWhyJoinItems().forEach(item -> {
                TutorBusinessWhyJoin e = new TutorBusinessWhyJoin();
                e.setBusiness(biz);
                e.setItem(item);
                biz.getWhyJoinItems().add(e);
            });
        }

        // ── Pricing includes ──────────────────────────────────────────────
        biz.getPricingIncludes().clear();
        if (dto.getPricingIncludes() != null) {
            dto.getPricingIncludes().forEach(item -> {
                TutorBusinessPricingInclude e = new TutorBusinessPricingInclude();
                e.setBusiness(biz);
                e.setItem(item);
                biz.getPricingIncludes().add(e);
            });
        }

        // ── Gallery ───────────────────────────────────────────────────────
        biz.getGalleryImages().clear();
        if (dto.getGalleryImages() != null) {
            dto.getGalleryImages().forEach(url -> {
                TutorBusinessGalleryImage e = new TutorBusinessGalleryImage();
                e.setBusiness(biz);
                e.setImageUrl(url);
                biz.getGalleryImages().add(e);
            });
        }

        // ── Business tutors ───────────────────────────────────────────────
        biz.getTutors().clear();
        if (dto.getBusinessTutors() != null) {
            dto.getBusinessTutors().forEach(t -> {
                BusinessTutor e = new BusinessTutor();
                e.setBusiness(biz);
                e.setName(t.getName());
                e.setImageUrl(t.getImageUrl());
                e.setSubjectsCsv(t.getSubjectsCsv());
                e.setGrades(t.getGrades());
                e.setRating(t.getRating());
                e.setReviewsCount(t.getReviewsCount());
                e.setExperienceYears(t.getExperienceYears());
                biz.getTutors().add(e);
            });
        }

        // ── Reviews ───────────────────────────────────────────────────────
        biz.getReviews().clear();
        if (dto.getReviews() != null) {
            dto.getReviews().forEach(r -> {
                TutorBusinessReview e = new TutorBusinessReview();
                e.setBusiness(biz);
                e.setStudent(r.getStudent());
                e.setRating(r.getRating());
                e.setComment(r.getComment());
                biz.getReviews().add(e);
            });
        }

        // ── Success stories ───────────────────────────────────────────────
        biz.getSuccessStories().clear();
        if (dto.getSuccessStories() != null) {
            dto.getSuccessStories().forEach(s -> {
                TutorBusinessSuccessStory e = new TutorBusinessSuccessStory();
                e.setBusiness(biz);
                e.setStudent(s.getStudent());
                e.setSubject(s.getSubject());
                e.setImprovement(s.getImprovement());
                e.setStory(s.getStory());
                biz.getSuccessStories().add(e);
            });
        }

        TutorBusiness saved = businessRepo.save(biz);
        return toDTO(saved);
    }

    // ── Mapper ────────────────────────────────────────────────────────────

    private TutorBusinessProfileDTO toDTO(TutorBusiness b) {
        return TutorBusinessProfileDTO.builder()
                .id(b.getId())
                // flat edit-form fields
                .name(b.getName())
                .headline(b.getHeadline())
                .mode(b.getMode())
                .location(b.getLocation())
                .bannerImageUrl(b.getBannerImageUrl())
                .bio(b.getBio())
                .pricePerMonth(b.getPricePerMonth())
                .currency(b.getCurrency())
                .subjectsOffered(b.getSubjectsOffered().stream()
                        .map(TutorBusinessSubjectOffered::getSubject).toList())
                .whyJoinItems(b.getWhyJoinItems().stream()
                        .map(TutorBusinessWhyJoin::getItem).toList())
                .pricingIncludes(b.getPricingIncludes().stream()
                        .map(TutorBusinessPricingInclude::getItem).toList())
                .galleryImages(b.getGalleryImages().stream()
                        .map(TutorBusinessGalleryImage::getImageUrl).toList())
                .businessTutors(b.getTutors().stream().map(t ->
                        TutorBusinessProfileDTO.BusinessTutorDTO.builder()
                                .id(t.getId())
                                .name(t.getName())
                                .imageUrl(t.getImageUrl())
                                .subjectsCsv(t.getSubjectsCsv())
                                .grades(t.getGrades())
                                .rating(t.getRating())
                                .reviewsCount(t.getReviewsCount())
                                .experienceYears(t.getExperienceYears())
                                .build()).toList())
                .reviews(b.getReviews().stream().map(r ->
                        TutorBusinessProfileDTO.ReviewDTO.builder()
                                .id(r.getId())
                                .student(r.getStudent())
                                .rating(r.getRating())
                                .comment(r.getComment())
                                .build()).toList())
                .successStories(b.getSuccessStories().stream().map(s ->
                        TutorBusinessProfileDTO.SuccessStoryDTO.builder()
                                .id(s.getId())
                                .student(s.getStudent())
                                .subject(s.getSubject())
                                .improvement(s.getImprovement())
                                .story(s.getStory())
                                .build()).toList())
                .build();
    }
}