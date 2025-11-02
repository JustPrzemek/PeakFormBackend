package com.peakform.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LabelDataPoint<T> {
    private String label;
    private T value;
}