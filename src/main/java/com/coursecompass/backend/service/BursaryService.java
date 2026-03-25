package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.repository.BursaryRepository;
import com.coursecompass.backend.repository.UserProfileRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BursaryService {

    private final BursaryRepository bursaryRepo;
    private final UserProfileRepository profileRepo; // optional matches engine

    public BursaryService(BursaryRepository bursaryRepo, UserProfileRepository profileRepo) {
        this.bursaryRepo = bursaryRepo;
        this.profileRepo = profileRepo;
    }

    public BursaryListResponseDTO list(
            String q,
            String status,
            String level,
            String fundingType,
            String field,
            String institutionType,
            Integer apsMin,
            Long incomeMax,
            int page,
            int size
    ) {
        Specification<Bursary> spec = (root, query, cb) -> cb.conjunction(); // ✅ always non-null

        spec = spec.and(BursarySpecifications.search(q));
        spec = spec.and(BursarySpecifications.hasStatus(parseEnum(status, BursaryStatus.class)));
        spec = spec.and(BursarySpecifications.hasStudyLevel(parseEnum(level, StudyLevel.class)));
        spec = spec.and(BursarySpecifications.hasFundingType(parseEnum(fundingType, FundingType.class)));
        spec = spec.and(BursarySpecifications.hasField(parseEnum(field, FieldCategory.class)));
        spec = spec.and(BursarySpecifications.hasInstitutionType(parseEnum(institutionType, InstitutionType.class)));
        spec = spec.and(BursarySpecifications.apsMinAtMost(apsMin));
        spec = spec.and(BursarySpecifications.incomeMaxAtLeast(incomeMax));


        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "closingDate"));
        Page<Bursary> paged = bursaryRepo.findAll(spec, pageable);

        FeaturedBursaryDTO featured = bursaryRepo.findBySlug("nsfas")
                .map(this::toFeaturedDTO)
                .orElse(null);

        FeaturedBursaryDTO featuredSecondary = bursaryRepo.findBySlug("funza-lushaka")
                .map(this::toFeaturedDTO)
                .orElse(null);

        // Collect IDs to exclude from grid
        Set<Long> featuredIds = new HashSet<>();
        if (featured != null) featuredIds.add(featured.getId());
        if (featuredSecondary != null) featuredIds.add(featuredSecondary.getId());

        List<BursaryCardDTO> items = paged.getContent().stream()
                .filter(b -> !featuredIds.contains(b.getId()))
                .map(this::toCardDTO)
                .toList();

        return BursaryListResponseDTO.builder()
                .featured(featured)
                .featuredSecondary(featuredSecondary)
                .items(items)
                .page(PageDTO.builder()
                        .number(paged.getNumber())
                        .size(paged.getSize())
                        .totalElements(paged.getTotalElements())
                        .totalPages(paged.getTotalPages())
                        .build())
                .build();
    }

    public BursaryDetailsDTO getDetails(Long id) {
        Bursary b = bursaryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bursary not found"));

        List<String> levels = b.getStudyLevels().stream().map(x -> x.getLevel().name()).toList();
        List<String> institutionTypes = b.getInstitutionTypes().stream().map(x -> x.getInstitutionType().name()).toList();
        List<String> fields = b.getFields().stream().map(x -> x.getField().name()).toList();

        List<String> covers = b.getCovers().stream().map(x -> x.getCover().name()).toList();
        List<String> requiredDocs = b.getRequiredDocuments().stream().map(BursaryRequiredDocument::getDocumentName).toList();

        // requirement bullets by category
        List<String> academic = bullets(b, RequirementCategory.ACADEMIC);
        List<String> financial = bullets(b, RequirementCategory.FINANCIAL);
        List<String> other = bullets(b, RequirementCategory.OTHER);

        List<FaqDTO> faqs = b.getFaqs().stream()
                .map(f -> FaqDTO.builder().q(f.getQuestion()).a(f.getAnswer()).build())
                .toList();

        List<String> steps = b.getApplySteps().stream()
                .sorted(Comparator.comparingInt(BursaryApplyStep::getStepOrder))
                .map(BursaryApplyStep::getStepText)
                .toList();

        return BursaryDetailsDTO.builder()
                .id(b.getId())
                .slug(b.getSlug())
                .name(b.getName())
                .provider(b.getProvider())
                .type(b.getType().name())
                .logoUrl(b.getLogoUrl())
                .status(b.getStatus().name())
                .openDate(b.getOpenDate())
                .closingDate(b.getClosingDate())
                .fundingType(b.getFundingType() == null ? null : b.getFundingType().name())

                .studyLevels(levels)
                .institutionTypes(institutionTypes)
                .institutions(List.of("ALL_PUBLIC_UNIVERSITIES")) // keep simple for now
                .fields(fields)

                .whatItCovers(WhatItCoversDTO.builder()
                        .covers(covers)
                        .notes("May include laptop allowance depending on the programme.")
                        .build())

                .requirements(RequirementsDTO.builder()
                        .citizenship(List.of("ZA"))
                        .apsMin(b.getMinAps())
                        .incomeMax(b.getIncomeMax())
                        .academic(academic)
                        .financial(financial)
                        .other(other)
                        .bonded(Boolean.TRUE.equals(b.getBonded()))
                        .build())

                .requiredDocuments(requiredDocs)

                .howToApply(HowToApplyDTO.builder()
                        .method("ONLINE")
                        .steps(steps)
                        .applyUrl(b.getApplyUrl())
                        .build())

                .faqs(faqs)
                .lastUpdated(b.getLastUpdated() == null ? null : b.getLastUpdated().atOffset(ZoneOffset.UTC))
                .build();
    }

    public BursaryFiltersDTO getFilters() {
        return BursaryFiltersDTO.builder()
                .statuses(Arrays.stream(BursaryStatus.values()).map(Enum::name).toList())
                .fundingTypes(Arrays.stream(FundingType.values()).map(Enum::name).toList())
                .studyLevels(Arrays.stream(StudyLevel.values()).map(Enum::name).toList())
                .institutionTypes(Arrays.stream(InstitutionType.values()).map(Enum::name).toList())
                .fields(Arrays.stream(FieldCategory.values()).map(Enum::name).toList())
                .build();
    }

    // Optional “You qualify” engine
    public BursaryMatchesDTO getMatches(User user) {
        var profileOpt = profileRepo.findByUserId(user.getId());
        if (profileOpt.isEmpty()) {
            return BursaryMatchesDTO.builder()
                    .user(UserSnapshotDTO.builder()
                            .aps(null)
                            .citizenship("ZA")
                            .householdIncome(null)
                            .level(null)
                            .institutionType(null)
                            .fieldsInterested(List.of())
                            .build())
                    .matches(List.of())
                    .build();
        }

        var p = profileOpt.get();
        var all = bursaryRepo.findAll();

        List<BursaryMatchItemDTO> matches = all.stream().map(b -> {
            List<String> reasons = new ArrayList<>();

            // APS check
            if (b.getMinAps() != null && p.getAps() != null && p.getAps() < b.getMinAps()) {
                reasons.add("APS too low (needs " + b.getMinAps() + ")");
            }

            // Income check
            if (b.getIncomeMax() != null && p.getHouseholdIncome() != null && p.getHouseholdIncome() > b.getIncomeMax()) {
                reasons.add("Household income too high (max " + b.getIncomeMax() + ")");
            }

            // Field check (if bursary has fields listed, user must match at least 1)
            Set<String> bursaryFields = b.getFields().stream().map(x -> x.getField().name()).collect(Collectors.toSet());
            Set<String> userFields = p.getFieldsInterested().stream().map(f -> f.getField().name()).collect(Collectors.toSet());
            if (!bursaryFields.isEmpty() && !userFields.isEmpty()) {
                boolean ok = bursaryFields.stream().anyMatch(userFields::contains);
                if (!ok) reasons.add("Field not supported");
            }

            String match;
            if (reasons.isEmpty()) match = "ELIGIBLE";
            else if (reasons.size() == 1) match = "MAYBE";
            else match = "NOT_ELIGIBLE";

            return BursaryMatchItemDTO.builder()
                    .bursaryId(b.getId())
                    .name(b.getName())
                    .match(match)
                    .reasons(reasons)
                    .build();
        }).toList();

        return BursaryMatchesDTO.builder()
                .user(UserSnapshotDTO.builder()
                        .aps(p.getAps())
                        .citizenship(p.getCitizenship())
                        .householdIncome(p.getHouseholdIncome())
                        .level(p.getStudyLevel() == null ? null : p.getStudyLevel().name())
                        .institutionType(p.getInstitutionType() == null ? null : p.getInstitutionType().name())
                        .fieldsInterested(p.getFieldsInterested().stream().map(f -> f.getField().name()).toList())
                        .build())
                .matches(matches)
                .build();
    }

    // ---------------- Mappers ----------------

    private FeaturedBursaryDTO toFeaturedDTO(Bursary b) {
        List<String> covers = b.getCovers().stream().map(x -> x.getCover().name()).toList();
        List<String> levels = b.getStudyLevels().stream().map(x -> x.getLevel().name()).toList();
        List<String> instTypes = b.getInstitutionTypes().stream().map(x -> x.getInstitutionType().name()).toList();

        return FeaturedBursaryDTO.builder()
                .id(b.getId())
                .slug(b.getSlug())
                .name(b.getName())
                .provider(b.getProvider())
                .type(b.getType().name())
                .logoUrl(b.getLogoUrl())
                .status(b.getStatus().name())
                .closingDate(b.getClosingDate())
                .summary(b.getSummary())
                .covers(covers)
                .eligibilitySnapshot(EligibilitySnapshotDTO.builder()
                        .citizenship(List.of("ZA"))
                        .incomeMax(Math.toIntExact(b.getIncomeMax()))
                        .institutions(instTypes)
                        .levels(levels)
                        .build())
                .applyUrl(b.getApplyUrl())
                .build();
    }

    private BursaryCardDTO toCardDTO(Bursary b) {
        return BursaryCardDTO.builder()
                .id(b.getId())
                .slug(b.getSlug())
                .name(b.getName())
                .provider(b.getProvider())
                .type(b.getType().name())
                .logoUrl(b.getLogoUrl())
                .status(b.getStatus().name())
                .closingDate(b.getClosingDate())
                .fundingType(b.getFundingType() == null ? null : b.getFundingType().name())
                .studyLevels(b.getStudyLevels().stream().map(x -> x.getLevel().name()).toList())
                .institutionTypes(b.getInstitutionTypes().stream().map(x -> x.getInstitutionType().name()).toList())
                .fields(b.getFields().stream().map(x -> x.getField().name()).toList())
                .summary(b.getSummary())
                .requirementsPreview(RequirementsPreviewDTO.builder()
                        .apsMin(b.getMinAps())
                        .citizenship(List.of("ZA"))
                        .incomeMax(Math.toIntExact(b.getIncomeMax()))
                        .bonded(Boolean.TRUE.equals(b.getBonded()))
                        .build())
                .build();
    }

    private static <T extends Enum<T>> T parseEnum(String value, Class<T> enumType) {
        if (value == null || value.isBlank()) return null;
        return Enum.valueOf(enumType, value.toUpperCase());
    }

    private List<String> bullets(Bursary b, RequirementCategory cat) {
        return b.getRequirementBullets().stream()
                .filter(x -> x.getCategory() == cat)
                .map(BursaryRequirementBullet::getBullet)
                .toList();
    }
}
