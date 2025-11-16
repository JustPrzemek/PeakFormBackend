package com.peakform.meals.dailyfoodlog.dto;

import com.peakform.meals.dailyfoodlog.enums.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AddFoodLogDto {

    @NotNull
    private String externalApiId; // ID z OpenFoodFacts

    @NotNull
    private LocalDate date; // Dzień, do którego logujemy

    @NotNull
    private MealType mealType; // ŚNIADANIE, OBIAD, etc.

    @Positive
    private double quantity; // np. 150 (g)
}
