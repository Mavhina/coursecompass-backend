package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.CreateSessionRequest;
import com.coursecompass.backend.dto.SessionDTO;
import com.coursecompass.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

    @Autowired private SessionRepository sessionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ExternalStudentRepository externalStudentRepository;
    @Autowired private StudentGroupRepository groupRepository;

    // ── Get sessions with optional filters ──
    public List<SessionDTO> getSessions(Long tutorId, String status, Integer year, Integer month) {
        List<Session> sessions;

        boolean hasStatus = status != null && !status.equalsIgnoreCase("all");
        boolean hasMonth  = year != null && month != null;

        // COMPLETED is computed — fetch all and filter after
        if (hasStatus && status.equalsIgnoreCase("COMPLETED")) {
            sessions = sessionRepository.findAllByTutorIdOrderByDateAscStartTimeAsc(tutorId)
                    .stream().filter(s -> isCompleted(s)).toList();
        } else if (hasStatus && hasMonth) {
            SessionStatus st = SessionStatus.valueOf(status.toUpperCase());
            sessions = sessionRepository.findByTutorIdAndStatusAndMonth(tutorId, st, year, month);
        } else if (hasStatus) {
            SessionStatus st = SessionStatus.valueOf(status.toUpperCase());
            sessions = sessionRepository.findByTutorIdAndStatus(tutorId, st);
        } else if (hasMonth) {
            sessions = sessionRepository.findByTutorIdAndMonth(tutorId, year, month);
        } else {
            sessions = sessionRepository.findAllByTutorIdOrderByDateAscStartTimeAsc(tutorId);
        }

        return sessions.stream().map(this::toDTO).toList();
    }

    // ── Create one or recurring sessions ──
    @Transactional
    public List<SessionDTO> createSessions(Long tutorId, CreateSessionRequest req) {
        List<Session> toSave = new ArrayList<>();
        String recurGroupId = req.isRecurring() ? UUID.randomUUID().toString() : null;

        if (req.isRecurring() && req.getRecurEndDate() != null) {
            List<LocalDate> dates = buildRecurringDates(req.getDate(), req.getRecurEndDate(), req.getRecurFrequency());
            for (LocalDate date : dates) {
                toSave.add(buildSession(tutorId, req, date, recurGroupId));
            }
        } else {
            toSave.add(buildSession(tutorId, req, req.getDate(), null));
        }

        return sessionRepository.saveAll(toSave).stream().map(this::toDTO).toList();
    }

    // ── Update status ──
    @Transactional
    public SessionDTO updateStatus(Long tutorId, Long sessionId, String status) {
        Session session = sessionRepository.findByIdAndTutorId(sessionId, tutorId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setStatus(SessionStatus.valueOf(status.toUpperCase()));
        return toDTO(sessionRepository.save(session));
    }

    // ── Helpers ──────────────────────────────────────────────

    private Session buildSession(Long tutorId, CreateSessionRequest req, LocalDate date, String recurGroupId) {
        return Session.builder()
                .tutorId(tutorId)
                .type(req.getType())
                .studentId(req.getStudentId())
                .externalStudentId(req.getExternalStudentId())
                .groupId(req.getGroupId())
                .subject(req.getSubject())
                .topic(req.getTopic())
                .date(date)
                .startTime(req.getStartTime())
                .duration(req.getDuration())
                .mode(req.getMode())
                .status(SessionStatus.PENDING)
                .notes(req.getNotes())
                .recurring(req.isRecurring())
                .recurGroupId(recurGroupId)
                .build();
    }

    private List<LocalDate> buildRecurringDates(LocalDate start, LocalDate end, String freq) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate cur = start;
        int maxSessions = 52;
        while (!cur.isAfter(end) && dates.size() < maxSessions) {
            dates.add(cur);
            cur = switch (freq == null ? "Weekly" : freq) {
                case "Daily"    -> cur.plusDays(1);
                case "Biweekly" -> cur.plusWeeks(2);
                case "Monthly"  -> cur.plusMonths(1);
                default         -> cur.plusWeeks(1); // Weekly
            };
        }
        return dates;
    }

    private boolean isCompleted(Session s) {
        LocalDateTime endDateTime = LocalDateTime.of(s.getDate(), s.getStartTime())
                .plusMinutes((long)(s.getDuration().doubleValue() * 60));
        return LocalDateTime.now().isAfter(endDateTime) && s.getStatus() != SessionStatus.CANCELLED;
    }

    private String computedStatus(Session s) {
        if (s.getStatus() == SessionStatus.CANCELLED) return "CANCELLED";
        if (isCompleted(s)) return "COMPLETED";
        return s.getStatus().name();
    }

    private SessionDTO toDTO(Session s) {
        String studentName = null;
        if (s.getStudentId() != null) {
            studentName = userRepository.findById(s.getStudentId())
                    .map(User::getFullName).orElse("Unknown");
        } else if (s.getExternalStudentId() != null) {
            studentName = externalStudentRepository.findById(s.getExternalStudentId())
                    .map(ExternalStudent::getName).orElse("Unknown");
        }

        String groupName = null;
        if (s.getGroupId() != null) {
            groupName = groupRepository.findById(s.getGroupId())
                    .map(StudentGroup::getName).orElse("Unknown Group");
        }

        return SessionDTO.builder()
                .id(s.getId())
                .type(s.getType())
                .studentId(s.getStudentId())
                .externalStudentId(s.getExternalStudentId())
                .studentName(studentName)
                .groupId(s.getGroupId())
                .groupName(groupName)
                .subject(s.getSubject())
                .topic(s.getTopic())
                .date(s.getDate())
                .startTime(s.getStartTime())
                .duration(s.getDuration())
                .mode(s.getMode())
                .status(s.getStatus())
                .computedStatus(computedStatus(s))
                .notes(s.getNotes())
                .recurring(s.isRecurring())
                .recurGroupId(s.getRecurGroupId())
                .build();
    }
}