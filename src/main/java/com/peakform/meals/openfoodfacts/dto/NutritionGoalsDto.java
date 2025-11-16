package com.peakform.meals.openfoodfacts.dto;

public record NutritionGoalsDto(
        int targetCalories,
        double targetProtein,
        double targetCarbs,
        double targetFat
) {}
