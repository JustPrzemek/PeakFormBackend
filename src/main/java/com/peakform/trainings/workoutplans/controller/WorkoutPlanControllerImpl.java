package com.peakform.trainings.workoutplans.controller;

import com.peakform.trainings.workoutplans.dto.AddExerciseToPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.CreateWorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.dto.UpdateExerciseInPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanDetailDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanSummaryDto;
import com.peakform.trainings.workoutplans.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkoutPlanControllerImpl implements WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @Override
    public ResponseEntity<WorkoutPlanDetailDto> generatePlan(PlanGenerationRequestDto requestDto) {
        WorkoutPlanDetailDto generatedPlan = workoutPlanService.generatePlan(requestDto);
        return new ResponseEntity<>(generatedPlan, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WorkoutPlanDetailDto> createEmptyPlan(CreateWorkoutPlanRequestDto requestDto) {
        WorkoutPlanDetailDto createdPlan = workoutPlanService.createEmptyPlan(requestDto);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<WorkoutPlanSummaryDto>> getUserPlans() {
        List<WorkoutPlanSummaryDto> plans = workoutPlanService.getUserPlans();
        return ResponseEntity.ok(plans);
    }

    @Override
    public ResponseEntity<WorkoutPlanDetailDto> getPlanDetails(Long planId) {
        WorkoutPlanDetailDto planDetails = workoutPlanService.getPlanDetails(planId);
        return ResponseEntity.ok(planDetails);
    }

    @Override
    public ResponseEntity<WorkoutPlanDetailDto> addExerciseToPlan(
            Long planId,
            AddExerciseToPlanRequestDto requestDto) {
        WorkoutPlanDetailDto updatedPlan = workoutPlanService.addExerciseToPlan(planId, requestDto);
        return ResponseEntity.ok(updatedPlan);
    }

    @Override
    public ResponseEntity<Void> deletePlan(Long planId) {
        workoutPlanService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> removeExerciseFromPlan(Long planId, Long workoutPlanExerciseId) {
        workoutPlanService.removeExerciseFromPlan(planId, workoutPlanExerciseId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<WorkoutPlanDetailDto> updateExerciseInPlan(
            Long planId,
            Long workoutPlanExerciseId,
            UpdateExerciseInPlanRequestDto requestDto) {
        WorkoutPlanDetailDto updatedPlan = workoutPlanService.updateExerciseInPlan(planId, workoutPlanExerciseId, requestDto);
        return ResponseEntity.ok(updatedPlan);
    }
}
