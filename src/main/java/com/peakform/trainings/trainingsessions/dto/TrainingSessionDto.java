package com.peakform.trainings.trainingsessions.dto;

import com.peakform.trainings.exerciselogs.dto.ExerciseLogDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrainingSessionDto {
    private Long id;
    private Long planId;
    private String planName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
    private List<ExerciseLogDto> logs;
}
