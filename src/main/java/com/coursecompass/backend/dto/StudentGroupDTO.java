package com.coursecompass.backend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter @Builder
public class StudentGroupDTO {
    private Long id;
    private String name;
    private String description;
    private String color;
    private List<StudentGroupMemberDTO> members;

    @Getter @Builder
    public static class StudentGroupMemberDTO {
        private Long memberId;        // student_group_members.id
        private Long studentId;
        private Long externalStudentId;
        private String name;
        private String email;
    }
}