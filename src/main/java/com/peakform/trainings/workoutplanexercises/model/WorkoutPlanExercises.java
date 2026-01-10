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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @Min(value = 1, message = "Sets must be at least 1")
    @Max(value = 100, message = "Sets cannot exceed 100")
    @Column(name = "sets")
    private Integer sets;

    @Min(value = 1, message = "Reps must be at least 1")
    @Max(value = 1000, message = "Reps cannot exceed 1000")
    @Column(name = "reps")
    private Integer reps;

    @Min(value = 0, message = "Rest time cannot be negative")
    @Max(value = 3600, message = "Rest time cannot exceed 60 minutes")
    @Column(name = "rest_time")
    private Integer restTime;

    @Size(min = 1, max = 50, message = "Day name must be between 1 and 50 characters")
    @Column(name = "day_identifier", nullable = false)
    private String dayIdentifier;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @PositiveOrZero(message = "Distance cannot be negative")
    @Max(value = 1000, message = "Distance cannot exceed 1000 km")
    @Column(name = "distance_km")
    private Float distanceKm;
}
