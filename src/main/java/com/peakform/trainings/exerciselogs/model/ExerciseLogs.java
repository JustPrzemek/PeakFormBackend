package com.peakform.trainings.exerciselogs.model;

import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "exercise_logs")
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSessions trainingSessions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercises exercises;

    @Column(name = "set_number")
    private Integer setNumber;

    @Min(value = 0, message = "Reps cannot be negative")
    @Max(value = 1000, message = "Reps cannot exceed 1000")
    @Column(name = "reps")
    private Integer reps;

    @PositiveOrZero(message = "Weight cannot be negative")
    @Max(value = 500, message = "Weight cannot exceed 500kg")
    @Column(name = "weight")
    private Float weight;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Min(value = 0, message = "Duration cannot be negative")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @PositiveOrZero(message = "Distance cannot be negative")
    @Max(value = 1000, message = "Distance cannot exceed 1000km")
    @Column(name = "distance_km")
    private Float distanceKm;
}
