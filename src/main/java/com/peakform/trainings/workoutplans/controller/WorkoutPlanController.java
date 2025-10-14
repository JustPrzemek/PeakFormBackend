package com.peakform.trainings.workoutplans.controller;

import com.peakform.trainings.workoutplanexercises.dto.PlanExerciseDetailsDto;
import com.peakform.trainings.workoutplans.dto.AddExerciseToPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.CreateWorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.dto.UpdateExerciseInPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanDetailDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/workout-plans")
@Tag(name = "Workout Plans", description = "API do zarządzania planami treningowymi")
public interface WorkoutPlanController {

    @PostMapping("/generate")
    @Operation(summary = "Generuje nowy plan treningowy dla użytkownika")
    ResponseEntity<WorkoutPlanDetailDto> generatePlan(@RequestBody PlanGenerationRequestDto requestDto);

    @PostMapping
    ResponseEntity<WorkoutPlanDetailDto> createEmptyPlan(@RequestBody @Valid CreateWorkoutPlanRequestDto requestDto);

    @GetMapping
    @Operation(summary = "Pobiera listę planów zalogowanego użytkownika")
    ResponseEntity<List<WorkoutPlanSummaryDto>> getUserPlans();

    @GetMapping("/{planId}")
    @Operation(summary = "Pobiera szczegóły konkretnego planu")
    ResponseEntity<WorkoutPlanDetailDto> getPlanDetails(@PathVariable Long planId);

    @PostMapping("/{planId}/exercises")
    ResponseEntity<WorkoutPlanDetailDto> addExerciseToPlan(
            @PathVariable Long planId,
            @RequestBody @Valid AddExerciseToPlanRequestDto requestDto);

    @DeleteMapping("/{planId}")
    @Operation(summary = "Usuwa plan treningowy")
    ResponseEntity<Void> deletePlan(@PathVariable Long planId);

    @DeleteMapping("/{planId}/exercises/{workoutPlanExerciseId}")
    ResponseEntity<Void> removeExerciseFromPlan(@PathVariable Long planId,
                                                @PathVariable Long workoutPlanExerciseId);

    @PutMapping("/{planId}/exercises/{workoutPlanExerciseId}")
    ResponseEntity<WorkoutPlanDetailDto> updateExerciseInPlan(
            @PathVariable Long planId,
            @PathVariable Long workoutPlanExerciseId,
            @RequestBody @Valid UpdateExerciseInPlanRequestDto requestDto);

    @GetMapping("/{planId}/days/{dayIdentifier}")
    ResponseEntity<List<PlanExerciseDetailsDto>> getExercisesForPlanDay(
            @PathVariable Long planId,
            @PathVariable String dayIdentifier);
}
