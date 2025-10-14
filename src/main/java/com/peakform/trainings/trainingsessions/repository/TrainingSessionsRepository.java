package com.peakform.trainings.trainingsessions.repository;

import com.peakform.security.user.model.User;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingSessionsRepository extends JpaRepository<TrainingSessions, Long> {

    Optional<TrainingSessions> findByUserAndEndTimeIsNull(User user);

}
