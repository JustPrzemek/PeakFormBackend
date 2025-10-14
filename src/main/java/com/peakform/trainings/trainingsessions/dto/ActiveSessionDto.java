package com.peakform.trainings.trainingsessions.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActiveSessionDto {
    private Long sessionId;
    private String planName;
    private String dayIdentifier;
}