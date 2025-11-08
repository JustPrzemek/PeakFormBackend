package com.peakform.statistics.controller;

import com.peakform.statistics.dto.KpiSummaryDTO;
import com.peakform.statistics.dto.LabelDataPoint;
import com.peakform.statistics.dto.TimeSeriesDataPoint;
import com.peakform.statistics.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsControllerImpl implements StatisticsController {

    private final StatisticService statisticsService;

    @Override
    public ResponseEntity<KpiSummaryDTO> getKpiSummary(LocalDateTime startDate, LocalDateTime endDate) {
        return ResponseEntity.ok(statisticsService.getKpiSummary(startDate, endDate));
    }

    @Override
    public ResponseEntity<List<TimeSeriesDataPoint<Long>>> getWorkoutFrequency(LocalDateTime startDate, LocalDateTime endDate) {
        return ResponseEntity.ok(statisticsService.getWorkoutFrequency(startDate, endDate));
    }

    @Override
    public ResponseEntity<List<TimeSeriesDataPoint<Double>>> getTotalVolumePerDay(LocalDateTime startDate, LocalDateTime endDate) {
        return ResponseEntity.ok(statisticsService.getTotalVolumePerDay(startDate, endDate));
    }

    @Override
    public ResponseEntity<List<TimeSeriesDataPoint<Double>>> getCardioDistancePerDay(LocalDateTime startDate, LocalDateTime endDate) {
        return ResponseEntity.ok(statisticsService.getCardioDistancePerDay(startDate, endDate));
    }

    @Override
    public ResponseEntity<List<LabelDataPoint<Long>>> getMuscleGroupDistribution(LocalDateTime startDate, LocalDateTime endDate) {
        return ResponseEntity.ok(statisticsService.getMuscleGroupDistribution(startDate, endDate));
    }

    @Override
    public ResponseEntity<List<TimeSeriesDataPoint<Double>>> getExerciseProgression(Long exerciseId, LocalDateTime startDate, LocalDateTime endDate) {
        return ResponseEntity.ok(statisticsService.getExerciseProgression(exerciseId, startDate, endDate));
    }
}
