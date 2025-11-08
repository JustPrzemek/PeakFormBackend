package com.peakform.trainings.workoutplans.controller;

import com.peakform.security.user.model.User;
import com.peakform.trainings.workoutplanexercises.dto.PlanExerciseDetailsDto;
import com.peakform.trainings.workoutplans.dto.AddExerciseToPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.CreateWorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.dto.UpdateExerciseInPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanDetailDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanSummaryDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanUpdateDto;
import com.peakform.trainings.workoutplans.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<WorkoutPlanDetailDto> generateBasicPlan() {
        WorkoutPlanDetailDto generatedBasicPlan = workoutPlanService.generateBasicPlan();
        return new ResponseEntity<>(generatedBasicPlan, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WorkoutPlanDetailDto> createEmptyPlan(CreateWorkoutPlanRequestDto requestDto) {
        WorkoutPlanDetailDto createdPlan = workoutPlanService.createEmptyPlan(requestDto);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<WorkoutPlanSummaryDto>> getUserPlans(String name, String goal, Boolean isActive, Pageable pageable) {
        Page<WorkoutPlanSummaryDto> plans = workoutPlanService.getUserPlans(name, goal, isActive, pageable);
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

    @Override
    public ResponseEntity<List<PlanExerciseDetailsDto>> getExercisesForPlanDay(
            Long planId,
            String dayIdentifier) {
        List<PlanExerciseDetailsDto> exercises = workoutPlanService.getExercisesForPlanDay(planId, dayIdentifier);
        return ResponseEntity.ok(exercises);
    }

    @Override
    public ResponseEntity<WorkoutPlanDetailDto> updatePlanDetails(
            Long planId,
            WorkoutPlanUpdateDto updateDto) {

        WorkoutPlanDetailDto updatedPlan = workoutPlanService.updatePlanDetails(planId, updateDto);
        return ResponseEntity.ok(updatedPlan);
    }
}
