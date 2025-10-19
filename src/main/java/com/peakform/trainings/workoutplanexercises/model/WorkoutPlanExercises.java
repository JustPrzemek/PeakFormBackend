package com.peakform.trainings.workoutplanexercises.model;

import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "workout_plan_exercises")
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanExercises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlans workoutPlans;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercises exercises;

    @Column(name = "sets")
    private Integer sets;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "rest_time")
    private Integer restTime;

    @Column(name = "day_identifier", nullable = false)
    private String dayIdentifier;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "distance_km")
    private Float distanceKm;
}
