package com.peakform.meals.weightlog.repository;

import com.peakform.meals.weightlog.model.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {

    List<WeightLog> findByUserIdOrderByDateDesc(Long userId);

    Optional<WeightLog> findByUserIdAndDate(Long userId, LocalDate date);
}