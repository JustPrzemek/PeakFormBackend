package com.peakform.trainings.trainingsessions.repository;

import com.peakform.security.user.model.User;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingSessionsRepository extends JpaRepository<TrainingSessions, Long> {

    Optional<TrainingSessions> findByUserAndEndTimeIsNull(User user);

    Optional<TrainingSessions> findFirstByUserAndEndTimeIsNotNullOrderByEndTimeDesc(User currentUser);

    Optional<TrainingSessions> findByIdAndUser(Long sessionId, User currentUser);

    Page<TrainingSessions> findAll(Specification<TrainingSessions> sessionWithSpec, Pageable pageable);
}
