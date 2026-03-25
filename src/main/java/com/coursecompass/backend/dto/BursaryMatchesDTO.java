package com.coursecompass.backend.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BursaryMatchesDTO {
    private UserSnapshotDTO user;
    private List<BursaryMatchItemDTO> matches;
}
