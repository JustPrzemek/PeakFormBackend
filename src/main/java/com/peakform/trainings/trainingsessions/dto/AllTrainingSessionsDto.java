package com.peakform.trainings.trainingsessions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllTrainingSessionsDto {
    private Long sessionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private String planName;
    private String dayIdentifier;
}
