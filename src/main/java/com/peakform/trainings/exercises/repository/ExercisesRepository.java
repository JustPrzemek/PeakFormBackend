package com.peakform.trainings.exercises.repository;

import com.peakform.trainings.exercises.model.Exercises;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExercisesRepository extends JpaRepository<Exercises, Long>, JpaSpecificationExecutor<Exercises> {
    Optional<Exercises> findByName(String name);
}
