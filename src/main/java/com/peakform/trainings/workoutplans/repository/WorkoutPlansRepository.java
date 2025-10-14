package com.peakform.trainings.workoutplans.repository;

import com.peakform.security.user.model.User;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutPlansRepository extends JpaRepository<WorkoutPlans, Long> {
    List<WorkoutPlans> findByUserId(Long id);

    List<WorkoutPlans> findByUser(User user);
}
