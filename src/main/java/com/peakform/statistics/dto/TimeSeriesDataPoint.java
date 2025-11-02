package com.peakform.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TimeSeriesDataPoint<T> {
    private LocalDate date;
    private T value;
}
