package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.*;
import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentGroupService {

    @Autowired private StudentGroupRepository groupRepository;
    @Autowired private StudentGroupMemberRepository memberRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ExternalStudentRepository externalStudentRepository;

    public List<StudentGroupDTO> getGroups(Long tutorId) {
        return groupRepository.findAllByTutorId(tutorId)
                .stream().map(this::toDTO).toList();
    }

    // ── NEW: get groups a student is a member of ──────────────────────────
    public List<StudentGroupDTO> getGroupsForStudent(Long studentId) {
        return groupRepository.findGroupsByStudentId(studentId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public StudentGroupDTO createGroup(Long tutorId, CreateGroupRequest req) {
        StudentGroup group = StudentGroup.builder()
                .tutorId(tutorId)
                .name(req.getName())
                .description(req.getDescription())
                .color(req.getColor() != null ? req.getColor() : "#6366f1")
                .build();
        groupRepository.save(group);

        if (req.getMembers() != null) {
            for (var m : req.getMembers()) {
                addMemberToGroup(group, m.getStudentId(), m.getExternalStudentId());
            }
        }
        return toDTO(groupRepository.findById(group.getId()).orElseThrow());
    }

    @Transactional
    public StudentGroupDTO addMembers(Long tutorId, Long groupId, List<CreateGroupRequest.GroupMemberRequest> members) {
        StudentGroup group = groupRepository.findByIdAndTutorId(groupId, tutorId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        for (var m : members) {
            addMemberToGroup(group, m.getStudentId(), m.getExternalStudentId());
        }
        return toDTO(groupRepository.findById(groupId).orElseThrow());
    }

    @Transactional
    public void removeMember(Long tutorId, Long groupId, Long memberId) {
        groupRepository.findByIdAndTutorId(groupId, tutorId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        memberRepository.deleteByGroupIdAndId(groupId, memberId);
    }

    @Transactional
    public void deleteGroup(Long tutorId, Long groupId) {
        StudentGroup group = groupRepository.findByIdAndTutorId(groupId, tutorId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        groupRepository.delete(group);
    }

    // ── Helpers ──────────────────────────────────────────────

    private void addMemberToGroup(StudentGroup group, Long studentId, Long externalStudentId) {
        StudentGroupMember member = StudentGroupMember.builder()
                .group(group)
                .studentId(studentId)
                .externalStudentId(externalStudentId)
                .build();
        memberRepository.save(member);
    }

    private StudentGroupDTO toDTO(StudentGroup group) {
        List<StudentGroupDTO.StudentGroupMemberDTO> members = group.getMembers().stream()
                .map(m -> {
                    String name = "Unknown";
                    String email = "";
                    if (m.getStudentId() != null) {
                        var user = userRepository.findById(m.getStudentId());
                        if (user.isPresent()) { name = user.get().getFullName(); email = user.get().getEmail(); }
                    } else if (m.getExternalStudentId() != null) {
                        var ext = externalStudentRepository.findById(m.getExternalStudentId());
                        if (ext.isPresent()) { name = ext.get().getName(); email = ext.get().getEmail() != null ? ext.get().getEmail() : ""; }
                    }
                    return StudentGroupDTO.StudentGroupMemberDTO.builder()
                            .memberId(m.getId())
                            .studentId(m.getStudentId())
                            .externalStudentId(m.getExternalStudentId())
                            .name(name)
                            .email(email)
                            .build();
                }).toList();

        return StudentGroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .color(group.getColor())
                .members(members)
                .build();
    }
}