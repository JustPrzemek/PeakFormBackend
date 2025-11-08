package com.peakform.trainings.workoutplans.repository;

import com.peakform.security.user.model.User;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WorkoutPlansRepository extends JpaRepository<WorkoutPlans, Long>, JpaSpecificationExecutor<WorkoutPlans> {
    List<WorkoutPlans> findByUserId(Long id);

    List<WorkoutPlans> findByUser(User user);
}
