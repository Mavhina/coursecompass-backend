package com.coursecompass.backend.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeFundConfigResponseDTO {
    private Boolean applicationsOpen;
    private String opensAt;
}
