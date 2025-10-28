package com.peakform.trainings.trainingsessions.dto;

import com.peakform.trainings.exerciselogs.dto.ExerciseLogDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpecificSessionWithLogsDto {
    private Long sessionId;
    private String notes;
    private String dayIdentifier;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private String planName;
    private List<ExerciseLogDto> excerciseLogsList;
}
