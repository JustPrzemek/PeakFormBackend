package com.peakform.trainings.exerciselogs.repository;

import com.peakform.trainings.exerciselogs.model.ExerciseLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseLogsRepository extends JpaRepository<ExerciseLogs, Long> {

}
