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
import com.peakform.trainings.workoutplans.dto.WorkoutPlanUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkoutPlanService {
    WorkoutPlanDetailDto generatePlan(PlanGenerationRequestDto requestDto);

    WorkoutPlanDetailDto createEmptyPlan(CreateWorkoutPlanRequestDto requestDto);

    WorkoutPlanDetailDto addExerciseToPlan(Long planId, AddExerciseToPlanRequestDto requestDto);

    Page<WorkoutPlanSummaryDto> getUserPlans(String name, String goal, Boolean isActive, Pageable pageable);

    WorkoutPlanDetailDto getPlanDetails(Long planId);

    WorkoutPlanDetailDto updateExerciseInPlan(Long planId, Long workoutPlanExerciseId, UpdateExerciseInPlanRequestDto requestDto);

    void removeExerciseFromPlan(Long planId, Long workoutPlanExerciseId);

    void deletePlan(Long planId);

    List<PlanExerciseDetailsDto> getExercisesForPlanDay(Long planId, String dayIdentifier);

    WorkoutPlanDetailDto updatePlanDetails(Long planId, WorkoutPlanUpdateDto dto);

}
