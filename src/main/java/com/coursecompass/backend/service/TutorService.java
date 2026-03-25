package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.Tutor;
import com.coursecompass.backend.domain.TutorBusiness;
import com.coursecompass.backend.dto.TutorCardDTO;
import com.coursecompass.backend.repository.TutorRepository;
import com.coursecompass.backend.repository.TutorBusinessRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final TutorBusinessRepository tutorBusinessRepository;

    public TutorService(TutorRepository tutorRepository,
                        TutorBusinessRepository tutorBusinessRepository) {
        this.tutorRepository = tutorRepository;
        this.tutorBusinessRepository = tutorBusinessRepository;
    }

    public List<TutorCardDTO> getAllTutorCards() {
        List<Tutor> tutors = tutorRepository.findAll();

        return tutors.stream().map(tutor -> {
            Long userId = tutor.getUser().getId();

            // Fetch the business for this tutor
            Optional<TutorBusiness> business = tutorBusinessRepository.findByOwner_Id(userId);
            Long businessId = business.map(TutorBusiness::getId).orElse(null);
            Integer price   = business.map(TutorBusiness::getPricePerMonth).orElse(tutor.getPricePerMonth());

            return TutorCardDTO.builder()
                    .user_id(userId)
                    .name(tutor.getUser().getFullName())
                    .subjects(
                            tutor.getTutorSubjects().stream()
                                    .map(ts -> ts.getSubject().getName())
                                    .distinct()
                                    .toList()
                    )
                    .grades(tutor.getGrades())
                    .pricePerMonth(price)
                    .rating(tutor.getRating())
                    .reviewsCount(tutor.getReviewsCount())
                    .mode(tutor.getMode())
                    .location(tutor.getLocation())
                    .imageUrl(tutor.getImageUrl())
                    .businessId(businessId)
                    .price(price)
                    .build();
        }).toList();
    }
}