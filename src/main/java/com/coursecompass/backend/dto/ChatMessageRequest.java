package com.coursecompass.backend.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long toStudentId;
    private String text;
    private String time;
}