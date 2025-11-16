package com.peakform.meals.openfoodfacts.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OffApiResponseV2Dto {
    @JsonProperty("product")
    private OffProductDto product;

    @JsonProperty("status")
    private int status;
}