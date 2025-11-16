package com.peakform.meals.dailyfoodlog.dto;

import com.peakform.meals.dailyfoodlog.enums.MealType;

import java.util.List;
import java.util.Map;

public record DailyLogDto(
        NutritionSummaryDto summary,
        Map<MealType, List<FoodLogEntryDto>> meals) {
}
