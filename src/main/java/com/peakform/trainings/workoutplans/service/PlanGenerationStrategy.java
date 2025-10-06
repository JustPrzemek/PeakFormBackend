package com.peakform.trainings.workoutplans.service;

import com.peakform.security.user.model.User;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;

public interface PlanGenerationStrategy {
    WorkoutPlans generatePlan(User user, PlanGenerationRequestDto request);
    boolean supports(PlanGenerationRequestDto request);
}
