package com.peakform.meals.dailyfoodlog.dto;

public record NutritionSummaryDto(
        double totalCalories,
        double totalProtein,
        double totalCarbs,
        double totalFat) {
}
