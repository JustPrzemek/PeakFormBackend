package com.peakform.meals.product.dto;

public record ProductSearchResultDto(
        String externalApiId,
        String name,
        String brand,
        double caloriesPer100g,
        double proteinPer100g,
        double carbsPer100g,
        double fatPer100g,
        String unit
) {}