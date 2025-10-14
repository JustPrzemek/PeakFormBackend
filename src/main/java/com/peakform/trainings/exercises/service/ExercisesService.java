package com.peakform.trainings.exercises.service;

import com.peakform.pages.PagedResponse;
import com.peakform.trainings.exercises.dto.ExerciseDto;
import com.peakform.trainings.exercises.dto.SingleExerciseDto;
import com.peakform.trainings.exercises.enums.Difficulty;

public interface ExercisesService {
    PagedResponse<ExerciseDto> getExercises(String name, String muscleGroup, Difficulty difficulty, int page, int size);

    SingleExerciseDto getExerciseById(Long id);
}
