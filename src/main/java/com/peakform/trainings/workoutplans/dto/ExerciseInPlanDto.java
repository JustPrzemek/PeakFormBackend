package com.peakform.trainings.workoutplans.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseInPlanDto {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private String exerciseType;
    private Integer sets;
    private Integer reps;
    private Integer restTime;
    private Integer durationMinutes;
    private Float distanceKm;

    public ExerciseInPlanDto(Long id,
                             Long exerciseId,
                             String exerciseName,
                             String exerciseType,
                             Integer sets,
                             Integer reps,
                             Integer restTime,
                             Integer durationMinutes,
                             Float distanceKm) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.exerciseType = exerciseType;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
        this.durationMinutes = durationMinutes;
        this.distanceKm = distanceKm;
    }
}
