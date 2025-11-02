package com.peakform.trainings.trainingsessions.repository;

import com.peakform.security.user.model.User;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionsRepository extends JpaRepository<TrainingSessions, Long> {

    Optional<TrainingSessions> findByUserAndEndTimeIsNull(User user);

    Optional<TrainingSessions> findFirstByUserAndEndTimeIsNotNullOrderByEndTimeDesc(User currentUser);

    Optional<TrainingSessions> findByIdAndUser(Long sessionId, User currentUser);

    Page<TrainingSessions> findAll(Specification<TrainingSessions> sessionWithSpec, Pageable pageable);

    @Query("SELECT COUNT(ts.id) FROM TrainingSessions ts WHERE ts.user.id = :userId AND ts.startTime BETWEEN :startDate AND :endDate")
    Long findWorkoutCountInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(ts.duration) FROM TrainingSessions ts WHERE ts.user.id = :userId AND ts.startTime BETWEEN :startDate AND :endDate")
    Long findTotalDurationInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT FUNCTION('DATE', ts.startTime) as day, COUNT(ts.id) as count " +
            "FROM TrainingSessions ts " +
            "WHERE ts.user.id = :userId AND ts.startTime BETWEEN :startDate AND :endDate " +
            "GROUP BY day ORDER BY day ASC")
    List<Object[]> findWorkoutFrequency(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
