package com.peakform.trainings.workoutplanexercises.repository;

import com.peakform.trainings.workoutplanexercises.model.WorkoutPlaneExercises;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlaneExercises, Long> {
    List<WorkoutPlaneExercises> findByWorkoutPlans(WorkoutPlans plan);

    @Query("SELECT wpe FROM WorkoutPlaneExercises wpe JOIN FETCH wpe.exercises WHERE wpe.workoutPlans.id = :planId")
    List<WorkoutPlaneExercises> findByWorkoutPlansId(@Param("planId") Long planId);
}
