package com.peakform.trainings.workoutplanexercises.repository;

import com.peakform.trainings.workoutplanexercises.model.WorkoutPlanExercises;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercises, Long> {
    List<WorkoutPlanExercises> findByWorkoutPlansAndDayIdentifier(WorkoutPlans plan, String dayIdentifier);

    @Query("SELECT wpe FROM WorkoutPlanExercises wpe JOIN FETCH wpe.exercises WHERE wpe.workoutPlans.id = :planId")
    List<WorkoutPlanExercises> findByWorkoutPlansId(@Param("planId") Long planId);

    @Query("SELECT DISTINCT wpe.dayIdentifier FROM WorkoutPlanExercises wpe WHERE wpe.workoutPlans.id = :planId ORDER BY wpe.dayIdentifier")
    List<String> findDistinctDayIdentifiersByWorkoutPlanId(@Param("planId") Long planId);

    @Query("SELECT wpe FROM WorkoutPlanExercises wpe JOIN FETCH wpe.exercises WHERE wpe.workoutPlans.id = :planId")
    List<WorkoutPlanExercises> findAllByWorkoutPlanIdWithExercises(@Param("planId") Long planId);

}
