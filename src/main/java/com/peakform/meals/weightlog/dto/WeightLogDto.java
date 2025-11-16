package com.peakform.meals.weightlog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WeightLogDto(

        @NotNull(message = "Weight cannot be null")
        @Positive(message = "Weight must be positive")
        Float weight

) {}
