package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.University;
import com.coursecompass.backend.dto.UniversityNameDTO;
import com.coursecompass.backend.repository.FacultyRepository;
import com.coursecompass.backend.service.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {

    private final UniversityService universityService;
    private final FacultyRepository facultyRepository;

    public UniversityController(UniversityService universityService, FacultyRepository facultyRepository) {
        this.universityService = universityService;
        this.facultyRepository = facultyRepository;
    }

    @PostMapping
    public ResponseEntity<University> createUniversity(@RequestBody University university) {
        return ResponseEntity.ok(universityService.saveUniversity(university));
    }

    @GetMapping
    public ResponseEntity<List<University>> getAllUniversities() {
        return ResponseEntity.ok(universityService.getAllUniversities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<University> getUniversityById(@PathVariable Long id) {
        University university = universityService.getUniversityById(id);
        if (university == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(university);
    }

    @PutMapping("/{id}")
    public ResponseEntity<University> updateUniversity(@PathVariable Long id, @RequestBody University university) {
        University updated = universityService.updateUniversity(id, university);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUniversity(@PathVariable Long id) {
        universityService.deleteUniversity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/names")
    public ResponseEntity<List<UniversityNameDTO>> getUniversityNames() {
        return ResponseEntity.ok(universityService.getUniversityNames());
    }

    @GetMapping("/{universityId}/faculties")
    public List<Map<String, Object>> faculties(@PathVariable Long universityId) {
        return facultyRepository
                .findByUniversityIdOrderByName(universityId)
                .stream()
                .map(f -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", f.getId());
                    map.put("name", f.getName());
                    return map;
                })
                .collect(Collectors.toList());
    }

}
