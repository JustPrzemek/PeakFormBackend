package com.peakform.meals.openfoodfacts.service;

import com.peakform.exceptions.ExternalApiException;
import com.peakform.exceptions.ProductNotFoundException;
import com.peakform.meals.openfoodfacts.dto.OffApiResponseDto;
import com.peakform.meals.openfoodfacts.dto.OffApiResponseV2Dto;
import com.peakform.meals.openfoodfacts.dto.OffProductDto;
import com.peakform.meals.product.dto.ProductSearchResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenFoodFactsService {

    private final WebClient openFoodFactsWebClient;

    private static final String OFF_REQUESTED_FIELDS = "_id,product_name,brands,serving_quantity_unit,nutriments";

    public List<ProductSearchResultDto> searchFood(String searchTerm) {

        OffApiResponseDto response;
        try {
            response = openFoodFactsWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cgi/search.pl")
                            .queryParam("search_terms", searchTerm)
                            .queryParam("search_simple", 1)
                            .queryParam("action", "process")
                            .queryParam("json", 1)
                            .queryParam("page_size", 10)
                            .queryParam("fields", OFF_REQUESTED_FIELDS)
                            .build())
                    .retrieve()
                    .bodyToMono(OffApiResponseDto.class)
                    .onErrorMap(WebClientResponseException.class, e -> {
                        log.error("Error searching food on OFF API: {}", e.getMessage());
                        return new ExternalApiException("Error searching OpenFoodFacts: " + e.getResponseBodyAsString(), e);
                    })
                    .block();

        } catch (Exception e) {
            log.error("Failed to call OFF search API", e);
            return Collections.emptyList();
        }

        if (response == null || response.getProducts() == null) {
            return Collections.emptyList();
        }

        return response.getProducts().stream()
                .map(this::mapToSearchResult)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public OffProductDto getFullProductById(String externalApiId) {

        OffApiResponseV2Dto response;
        try {
            response = openFoodFactsWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v2/product/{id}")
                            .queryParam("fields", OFF_REQUESTED_FIELDS)
                            .build(externalApiId))
                    .retrieve()
                    .bodyToMono(OffApiResponseV2Dto.class)
                    .onErrorMap(WebClientResponseException.class, e -> {
                        log.error("Error fetching product {} from OFF API: {}", externalApiId, e.getMessage());
                        return new ExternalApiException("Error from OpenFoodFacts: " + e.getResponseBodyAsString(), e);
                    })
                    .block();

        } catch (Exception e) {
            log.error("Failed to call OFF getProduct API for id {}", externalApiId, e);
            throw new ExternalApiException("Failed to fetch product data: " + e.getMessage(), e);
        }

        if (response == null || response.getProduct() == null || response.getStatus() == 0) {
            log.warn("Product not found on OpenFoodFacts: {}", externalApiId);
            throw new ProductNotFoundException("Product not found on OpenFoodFacts with ID: " + externalApiId);
        }

        return response.getProduct();
    }

    private Optional<ProductSearchResultDto> mapToSearchResult(OffProductDto offProduct) {
        if (offProduct.getNutriments() == null ||
                offProduct.getNutriments().getCaloriesPer100g() == null ||
                offProduct.getNutriments().getProteinPer100g() == null ||
                offProduct.getNutriments().getCarbsPer100g() == null ||
                offProduct.getNutriments().getFatPer100g() == null) {

            log.warn("Product {} (ID: {}) skipped. Missing nutriments.", offProduct.getName(), offProduct.getExternalApiId());
            return Optional.empty();
        }

        return Optional.of(new ProductSearchResultDto(
                offProduct.getExternalApiId(),
                Optional.ofNullable(offProduct.getName()).orElse("Brak nazwy"),
                Optional.ofNullable(offProduct.getBrand()).orElse("Brak marki"),
                offProduct.getNutriments().getCaloriesPer100g(),
                offProduct.getNutriments().getProteinPer100g(),
                offProduct.getNutriments().getCarbsPer100g(),
                offProduct.getNutriments().getFatPer100g(),
                Optional.ofNullable(offProduct.getUnit()).orElse("g")
        ));
    }
}
