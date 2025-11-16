package com.peakform.meals.weightlog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

public record WeightHistoryDto(LocalDate date, Float weight) {
}
