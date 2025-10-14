package com.peakform.trainings.workoutplans.service;

import com.peakform.security.user.model.User;
import com.peakform.trainings.workoutplanexercises.dto.PlanExerciseDetailsDto;
import com.peakform.trainings.workoutplans.dto.AddExerciseToPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.CreateWorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.dto.UpdateExerciseInPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanDetailDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanSummaryDto;

import java.util.List;

public interface WorkoutPlanService {
    WorkoutPlanDetailDto generatePlan(PlanGenerationRequestDto requestDto);

    WorkoutPlanDetailDto createEmptyPlan(CreateWorkoutPlanRequestDto requestDto);

    WorkoutPlanDetailDto addExerciseToPlan(Long planId, AddExerciseToPlanRequestDto requestDto);

    List<WorkoutPlanSummaryDto> getUserPlans();

    WorkoutPlanDetailDto getPlanDetails(Long planId);

    WorkoutPlanDetailDto updateExerciseInPlan(Long planId, Long workoutPlanExerciseId, UpdateExerciseInPlanRequestDto requestDto);

    void removeExerciseFromPlan(Long planId, Long workoutPlanExerciseId);

    void deletePlan(Long planId);

    List<PlanExerciseDetailsDto> getExercisesForPlanDay(Long planId, String dayIdentifier);

}
