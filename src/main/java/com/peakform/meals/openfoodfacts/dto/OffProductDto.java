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
public class OffProductDto {

    @JsonProperty("_id")
    private String externalApiId; // kod kreskotwy to

    @JsonProperty("product_name")
    private String name;

    @JsonProperty("brands")
    private String brand;

    @JsonProperty("nutriments")
    private OffNutrimentsDto nutriments;

    @JsonProperty("serving_quantity_unit")
    private String unit; // 'g' lub 'ml'
}
