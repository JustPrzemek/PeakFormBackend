package com.peakform.trainings.workoutplans.repository;

import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlans, Long> {
    List<WorkoutPlans> findByUserId(Long id);
}
