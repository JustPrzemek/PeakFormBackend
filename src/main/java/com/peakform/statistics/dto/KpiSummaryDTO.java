package com.peakform.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiSummaryDTO {
    private Long totalWorkouts;
    private Long totalDurationSeconds;
    private Double totalVolume;
    private Long totalSets;
    private Double totalDistanceKm;
}
