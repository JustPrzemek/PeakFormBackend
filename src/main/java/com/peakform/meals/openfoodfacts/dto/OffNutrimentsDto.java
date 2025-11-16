package com.peakform.meals.openfoodfacts.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OffNutrimentsDto {

    @JsonProperty("energy-kcal_100g")
    private Double caloriesPer100g;

    @JsonProperty("proteins_100g")
    private Double proteinPer100g;

    @JsonProperty("carbohydrates_100g")
    private Double carbsPer100g;

    @JsonProperty("fat_100g")
    private Double fatPer100g;
}
