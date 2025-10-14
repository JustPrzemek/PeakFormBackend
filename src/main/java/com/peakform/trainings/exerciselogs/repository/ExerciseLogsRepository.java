package com.peakform.trainings.exerciselogs.repository;

import com.peakform.security.user.model.User;
import com.peakform.trainings.exerciselogs.model.ExerciseLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseLogsRepository extends JpaRepository<ExerciseLogs, Long> {

    @Query("SELECT el FROM ExerciseLogs el " +
            "JOIN el.trainingSessions ts " +
            "WHERE ts.user = :user " +
            "AND el.exercises.id = :exerciseId " +
            "ORDER BY el.createdAt DESC")
    Optional<ExerciseLogs> findTopByUserAndExerciseOrderByCreatedAtDesc(User user, Long exerciseId);
}
