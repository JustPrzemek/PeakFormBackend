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

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "distance_km")
    private Float distanceKm;
}
