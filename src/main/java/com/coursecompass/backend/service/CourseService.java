package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.Course;
import com.coursecompass.backend.domain.CourseRequirementGroup;
import com.coursecompass.backend.domain.CourseRequirementItem;
import com.coursecompass.backend.dto.CourseDTO;
import com.coursecompass.backend.dto.CourseResponseDTO;
import com.coursecompass.backend.repository.CourseRepository;
import com.coursecompass.backend.repository.CourseRequirementGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseRequirementGroupRepository groupRepository;

    // ✅ MAIN: all courses for a university (ALL faculties)
    public List<CourseResponseDTO> getCoursesByUniversity(Long universityId) {
        List<Course> courses = courseRepository.findByUniversityId(universityId);
        return buildCourseResponse(courses);
    }

    // ✅ Optional: only if you want to filter by faculty on frontend
    public List<CourseResponseDTO> getCoursesByUniversityAndFaculty(Long universityId, Long facultyId) {
        List<Course> courses = courseRepository.findByUniversityIdAndFacultyId(universityId, facultyId);
        return buildCourseResponse(courses);
    }

    // ✅ shared builder (your same logic, just extracted)
    private List<CourseResponseDTO> buildCourseResponse(List<Course> courses) {
        List<CourseResponseDTO> result = new ArrayList<>();

        for (Course course : courses) {

            List<CourseRequirementGroup> groups =
                    groupRepository.findByCourseIdOrderBySortOrderAsc(course.getId());

            List<Object> requirements = new ArrayList<>();

            for (CourseRequirementGroup group : groups) {

                // safety: skip empty group
                if (group.getItems() == null || group.getItems().isEmpty()) continue;

                if (group.getType() == CourseRequirementGroup.GroupType.SINGLE) {
                    CourseRequirementItem item = group.getItems().get(0);

                    Map<String, Object> req = new LinkedHashMap<>();
                    req.put("subject", item.getSubject().getName());
                    req.put("minPercentage", item.getMinPercentage());

                    requirements.add(req);

                } else {
                    // ANY_OF
                    List<Map<String, Object>> anyOfList = new ArrayList<>();

                    for (CourseRequirementItem item : group.getItems()) {
                        Map<String, Object> opt = new LinkedHashMap<>();
                        opt.put("subject", item.getSubject().getName());
                        opt.put("minPercentage", item.getMinPercentage());
                        if (item.getMinAps() != null) opt.put("minAps", item.getMinAps());
                        anyOfList.add(opt);
                    }

                    Map<String, Object> anyOfWrapper = new LinkedHashMap<>();
                    anyOfWrapper.put("anyOf", anyOfList);

                    requirements.add(anyOfWrapper);
                }
            }

            CourseDTO courseDTO = CourseDTO.builder()
                    // if you have id field in CourseDTO add: .id(course.getId())
                    .name(course.getName())
                    .years(course.getYears())
                    .minAps(course.getMinAps())
                    .minApsRule(course.getMinApsRule())
                    .faculty(course.getFaculty() != null ? course.getFaculty().getName() : null)
                    .inDemand(course.getInDemand())
                    .description(course.getDescription())
                    .build();

            result.add(CourseResponseDTO.builder()
                    .course(courseDTO)
                    .requirements(requirements)
                    .build());
        }

        return result;
    }
}
