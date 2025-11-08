package com.peakform.trainings.exerciselogs.repository;

import com.peakform.trainings.exerciselogs.model.ExerciseLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseLogsRepository extends JpaRepository<ExerciseLogs, Long> {

    @Query("SELECT SUM(el.weight * el.reps) FROM ExerciseLogs el " +
            "JOIN el.trainingSessions ts JOIN el.exercises ex " +
            "WHERE ts.user.id = :userId AND ex.type = 'STRENGTH' AND ts.startTime BETWEEN :startDate AND :endDate")
    Double findTotalVolumeInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(el.id) FROM ExerciseLogs el " +
            "JOIN el.trainingSessions ts JOIN el.exercises ex " +
            "WHERE ts.user.id = :userId AND ex.type = 'STRENGTH' AND ts.startTime BETWEEN :startDate AND :endDate")
    Long findTotalSetsInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(el.distanceKm) FROM ExerciseLogs el " +
            "JOIN el.trainingSessions ts JOIN el.exercises ex " +
            "WHERE ts.user.id = :userId AND ex.type = 'CARDIO' AND ts.startTime BETWEEN :startDate AND :endDate")
    Double findTotalCardioDistanceInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("SELECT FUNCTION('DATE', ts.startTime) as day, SUM(el.weight * el.reps) as totalVolume " +
            "FROM ExerciseLogs el JOIN el.trainingSessions ts JOIN el.exercises ex " +
            "WHERE ts.user.id = :userId AND ex.type = 'STRENGTH' " +
            "AND ts.startTime BETWEEN :startDate AND :endDate AND el.weight IS NOT NULL AND el.reps IS NOT NULL " +
            "GROUP BY day ORDER BY day ASC")
    List<Object[]> findTotalVolumePerDay(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Wykres C: Dystans Cardio
    @Query("SELECT FUNCTION('DATE', ts.startTime) as day, SUM(el.distanceKm) as totalDistance " +
            "FROM ExerciseLogs el JOIN el.trainingSessions ts JOIN el.exercises ex " +
            "WHERE ts.user.id = :userId AND ex.type = 'CARDIO' " +
            "AND ts.startTime BETWEEN :startDate AND :endDate AND el.distanceKm IS NOT NULL " +
            "GROUP BY day ORDER BY day ASC")
    List<Object[]> findCardioDistancePerDay(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Wykres 3.A: Rozkład Grup Mięśniowych
    @Query("SELECT ex.muscleGroup as muscle, COUNT(el.id) as setCount " +
            "FROM ExerciseLogs el JOIN el.trainingSessions ts JOIN el.exercises ex " +
            "WHERE ts.user.id = :userId AND ex.type = 'STRENGTH' " +
            "AND ts.startTime BETWEEN :startDate AND :endDate " +
            "GROUP BY ex.muscleGroup")
    List<Object[]> findMuscleGroupDistribution(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Wykres 4.A: Progresja w Ćwiczeniu
    @Query("SELECT FUNCTION('DATE', ts.startTime) as day, MAX(el.weight) as maxWeight " +
            "FROM ExerciseLogs el JOIN el.trainingSessions ts " +
            "WHERE ts.user.id = :userId AND el.exercises.id = :exerciseId " +
            "AND ts.startTime BETWEEN :startDate AND :endDate AND el.weight IS NOT NULL " +
            "GROUP BY day ORDER BY day ASC")
    List<Object[]> findExerciseProgression(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
