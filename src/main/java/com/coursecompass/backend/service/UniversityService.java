package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.University;
import com.coursecompass.backend.dto.UniversityNameDTO;
import com.coursecompass.backend.repository.UniversityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UniversityService {
    private final UniversityRepository universityRepository;

    public UniversityService(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    public University saveUniversity(University university) {
        return universityRepository.save(university);
    }

    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public University getUniversityById(Long id) {
        return universityRepository.findById(id).orElse(null);
    }

    public University updateUniversity(Long id, University universityDetails) {
        University university = getUniversityById(id);
        if (university != null) {
            university.setName(universityDetails.getName());
            university.setLocation(universityDetails.getLocation());
            return universityRepository.save(university);
        }
        return null;
    }

    public List<UniversityNameDTO> getUniversityNames() {
        return universityRepository.findAllUniversityNames();
    }

    public void deleteUniversity(Long id) {
        universityRepository.deleteById(id);
    }
}
