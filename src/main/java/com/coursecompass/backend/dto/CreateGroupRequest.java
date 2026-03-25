package com.coursecompass.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class CreateGroupRequest {
    @NotBlank(message = "Group name is required")
    private String name;
    private String description;
    private String color;
    private List<GroupMemberRequest> members;

    @Getter @Setter
    public static class GroupMemberRequest {
        private Long studentId;
        private Long externalStudentId;
    }
}