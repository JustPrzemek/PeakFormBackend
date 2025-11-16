package com.peakform.meals.dailyfoodlog.dto;

public record FoodLogEntryDto(
        long logId,
        String name,
        String brand,
        double quantity,
        String unit,
        double caloriesEaten,
        double proteinEaten,
        double carbsEaten,
        double fatEaten) {
}