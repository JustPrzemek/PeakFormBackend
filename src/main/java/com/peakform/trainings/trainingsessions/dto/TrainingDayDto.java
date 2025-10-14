package com.peakform.trainings.trainingsessions.dto;

import com.peakform.trainings.exerciselogs.dto.ExerciseLogDto;
import lombok.Data;

import java.util.List;

@Data
public class TrainingDayDto {
    private Long sessionId;
    private String planName;
    private String dayIdentifier;
    private List<PlanExerciseDto> exercises;
    private List<ExerciseLogDto> completedLogs;
}
