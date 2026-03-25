package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.*;
import com.coursecompass.backend.service.StudentGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tutor/groups")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StudentGroupController {

    @Autowired private StudentGroupService groupService;

    private Long getTutorId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }

    @GetMapping
    public ApiResponse<List<StudentGroupDTO>> getGroups(Authentication auth) {
        return ApiResponse.success(groupService.getGroups(getTutorId(auth)));
    }

    @PostMapping
    public ApiResponse<StudentGroupDTO> createGroup(
            Authentication auth,
            @Valid @RequestBody CreateGroupRequest request) {
        return ApiResponse.success(groupService.createGroup(getTutorId(auth), request));
    }

    @PostMapping("/{groupId}/members")
    public ApiResponse<StudentGroupDTO> addMembers(
            Authentication auth,
            @PathVariable Long groupId,
            @RequestBody List<CreateGroupRequest.GroupMemberRequest> members) {
        return ApiResponse.success(groupService.addMembers(getTutorId(auth), groupId, members));
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ApiResponse<String> removeMember(
            Authentication auth,
            @PathVariable Long groupId,
            @PathVariable Long memberId) {
        groupService.removeMember(getTutorId(auth), groupId, memberId);
        return ApiResponse.success("Member removed");
    }

    @DeleteMapping("/{groupId}")
    public ApiResponse<String> deleteGroup(
            Authentication auth,
            @PathVariable Long groupId) {
        groupService.deleteGroup(getTutorId(auth), groupId);
        return ApiResponse.success("Group deleted");
    }
}