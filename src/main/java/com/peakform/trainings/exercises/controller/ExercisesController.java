package com.peakform.trainings.exercises.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.trainings.exercises.dto.ExerciseDto;
import com.peakform.trainings.exercises.dto.SingleExerciseDto;
import com.peakform.trainings.exercises.enums.Difficulty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/exercises")
@Tag(name = "Exercises", description = "API do zarządzania bazą ćwiczeń")
public interface ExercisesController {

    @GetMapping
    @Operation(
            summary = "Pobierz listę ćwiczeń z filtrowaniem i paginacją",
            description = "Zwraca stronę z listą ćwiczeń. Możesz filtrować wyniki po nazwie, grupie mięśniowej i poziomie trudności."
    )
    ResponseEntity<PagedResponse<ExerciseDto>> getExercises(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String muscleGroup,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @GetMapping("/{id}")
    @Operation(
            summary = "Pobierz szczegóły jednego ćwiczenia",
            description = "Zwraca pełne informacje o ćwiczeniu na podstawie jego ID."
    )
    ResponseEntity<SingleExerciseDto> getExerciseById(@PathVariable Long id);
}
