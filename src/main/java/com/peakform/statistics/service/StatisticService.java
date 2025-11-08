package com.peakform.statistics.service;

import com.peakform.statistics.dto.KpiSummaryDTO;
import com.peakform.statistics.dto.LabelDataPoint;
import com.peakform.statistics.dto.TimeSeriesDataPoint;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

//    tutaj sobie opisalem co jaka metoda robi na przyszłośc
    /**
     * Pobiera kluczowe wskaźniki (KPI) dla zalogowanego użytkownika w danym okresie.
     */
    KpiSummaryDTO getKpiSummary(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Pobiera częstotliwość treningów (ilość sesji dziennie) dla zalogowanego użytkownika.
     */
    List<TimeSeriesDataPoint<Long>> getWorkoutFrequency(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Pobiera całkowitą objętość (waga * powtórzenia) dziennie dla zalogowanego użytkownika.
     */
    List<TimeSeriesDataPoint<Double>> getTotalVolumePerDay(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Pobiera całkowity dystans cardio dziennie dla zalogowanego użytkownika.
     */
    List<TimeSeriesDataPoint<Double>> getCardioDistancePerDay(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Pobiera rozkład wykonanych serii na grupy mięśniowe dla zalogowanego użytkownika.
     */
    List<LabelDataPoint<Long>> getMuscleGroupDistribution(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Pobiera progresję maksymalnego ciężaru dla wybranego ćwiczenia dla zalogowanego użytkownika.
     */
    List<TimeSeriesDataPoint<Double>> getExerciseProgression(Long exerciseId, LocalDateTime startDate, LocalDateTime endDate);

}
