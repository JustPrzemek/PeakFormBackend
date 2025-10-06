package com.peakform.trainings.exercises.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.trainings.exercises.dto.ExerciseDto;
import com.peakform.trainings.exercises.dto.SingleExerciseDto;
import com.peakform.trainings.exercises.enums.Difficulty;
import com.peakform.trainings.exercises.service.ExercisesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExercisesControllerImpl implements ExercisesController {

    private final ExercisesService exercisesService;

    @Override
    public ResponseEntity<PagedResponse<ExerciseDto>> getExercises(
            String name,
            String muscleGroup,
            Difficulty difficulty,
            int page,
            int size) {
        PagedResponse<ExerciseDto> exercises = exercisesService.getExercises(name, muscleGroup, difficulty, page, size);
        return ResponseEntity.ok(exercises);
    }

    @Override
    public ResponseEntity<SingleExerciseDto> getExerciseById(Long id) {
        SingleExerciseDto exercise = exercisesService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }
}
