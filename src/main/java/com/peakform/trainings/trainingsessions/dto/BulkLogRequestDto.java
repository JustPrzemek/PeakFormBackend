package com.peakform.trainings.trainingsessions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BulkLogRequestDto {
    @NotNull
    private Long planId;
    @NotBlank
    private String dayIdentifier;
    @NotNull
    private LocalDateTime workoutDateStart;
    @NotNull
    private LocalDateTime workoutDateEnd;
    private String notes;
    @NotEmpty
    private List<BulkExerciseLogDto> exercises;
}
