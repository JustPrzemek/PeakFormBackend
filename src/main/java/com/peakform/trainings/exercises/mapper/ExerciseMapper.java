package com.peakform.trainings.exercises.mapper;

import com.peakform.trainings.exercises.dto.ExerciseDto;
import com.peakform.trainings.exercises.dto.SingleExerciseDto;
import com.peakform.trainings.exercises.model.Exercises;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    public ExerciseDto toExerciseDto(Exercises exercise) {
        if (exercise == null) {
            return null;
        }
        return new ExerciseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getMuscleGroup(),
                exercise.getDifficulty().name()
        );
    }

    public SingleExerciseDto toSingleExerciseDto(Exercises exercise) {
        if (exercise == null) {
            return null;
        }
        return new SingleExerciseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getMuscleGroup(),
                exercise.getDifficulty().name(),
                exercise.getDescription(),
                exercise.getVideoUrl()
        );
    }
}
