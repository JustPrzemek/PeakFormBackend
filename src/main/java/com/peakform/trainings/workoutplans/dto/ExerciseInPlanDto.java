package com.peakform.trainings.workoutplans.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseInPlanDto {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private int sets;
    private int reps;
    private int restTime;

    public ExerciseInPlanDto(Long id, Long exerciseId, String exerciseName, int sets, int reps, int restTime) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
    }
}
