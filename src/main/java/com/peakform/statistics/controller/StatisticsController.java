package com.peakform.statistics.controller;

import com.peakform.statistics.dto.KpiSummaryDTO;
import com.peakform.statistics.dto.LabelDataPoint;
import com.peakform.statistics.dto.TimeSeriesDataPoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "uzupelnic pozniej")
public interface StatisticsController {

    @GetMapping("/kpi")
    @Operation(summary = "Get Key Performance Indicators (KPIs) for the logged-in user")
    ResponseEntity<KpiSummaryDTO> getKpiSummary(
            @Parameter(description = "Start date and time (ISO format)", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date and time (ISO format)", example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    );

    @GetMapping("/frequency")
    @Operation(summary = "Get workout frequency chart data for the logged-in user")
    ResponseEntity<List<TimeSeriesDataPoint<Long>>> getWorkoutFrequency(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    );

    @GetMapping("/volume")
    @Operation(summary = "Get total volume (weight * reps) chart data for the logged-in user")
    ResponseEntity<List<TimeSeriesDataPoint<Double>>> getTotalVolumePerDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    );

    @GetMapping("/cardio-distance")
    @Operation(summary = "Get total cardio distance chart data for the logged-in user")
    ResponseEntity<List<TimeSeriesDataPoint<Double>>> getCardioDistancePerDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    );

    @GetMapping("/muscle-distribution")
    @Operation(summary = "Get muscle group distribution (pie chart) data for the logged-in user")
    ResponseEntity<List<LabelDataPoint<Long>>> getMuscleGroupDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    );

    @GetMapping("/progression/{exerciseId}")
    @Operation(summary = "Get exercise progression (max weight) chart data for a specific exercise")
    ResponseEntity<List<TimeSeriesDataPoint<Double>>> getExerciseProgression(
            @PathVariable Long exerciseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    );
}
