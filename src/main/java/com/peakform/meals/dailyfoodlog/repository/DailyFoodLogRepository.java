package com.peakform.meals.dailyfoodlog.repository;

import com.peakform.meals.dailyfoodlog.model.DailyFoodLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyFoodLogRepository extends JpaRepository<DailyFoodLog, Long> {

    List<DailyFoodLog> findByUserIdAndDate(Long userId, LocalDate date);
}
