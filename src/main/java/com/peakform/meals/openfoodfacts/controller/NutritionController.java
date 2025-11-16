package com.peakform.meals.openfoodfacts.controller;

import com.peakform.meals.dailyfoodlog.dto.AddFoodLogDto;
import com.peakform.meals.dailyfoodlog.dto.DailyLogDto;
import com.peakform.meals.dailyfoodlog.service.DailyFoodLogService;
import com.peakform.meals.openfoodfacts.dto.NutritionGoalsDto;
import com.peakform.meals.openfoodfacts.service.NutritionGoalService;
import com.peakform.meals.openfoodfacts.service.OpenFoodFactsService;
import com.peakform.meals.product.dto.ProductSearchResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
@Validated // Wymagane do walidacji parametrów @RequestParam (jak w searchFood)
@Tag(name = "Nutrition", description = "Endpoints for managing nutrition, goals, and food logs")
public class NutritionController {

    private final NutritionGoalService nutritionGoalService;
    private final OpenFoodFactsService openFoodFactsService;
    private final DailyFoodLogService dailyFoodLogService;

    /**
     * Endpoint Dnia 1: Obliczanie celów żywieniowych
     */
    @Operation(summary = "Get nutritional goals", description = "Calculates and retrieves the authenticated user's daily nutritional goals (calories, protein, carbs, fat).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goals calculated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NutritionGoalsDto.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/goals")
    public ResponseEntity<NutritionGoalsDto> getNutritionGoals(Principal principal) {
        NutritionGoalsDto goals = nutritionGoalService.calculateUserGoals(principal.getName());
        return ResponseEntity.ok(goals);
    }

    /**
     * Endpoint Dnia 1: Wyszukiwarka produktów (proxy do OFF)
     */
    @Operation(summary = "Search for food products", description = "Searches for food products using an external API (like OpenFoodFacts).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductSearchResultDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid search query (e.g., less than 3 characters)", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductSearchResultDto>> searchFood(
            @Parameter(description = "Search term (min 3 characters)", required = true, example = "chicken breast")
            @RequestParam @NotBlank @Size(min = 3) String query) {

        List<ProductSearchResultDto> results = openFoodFactsService.searchFood(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint Dnia 2: Logowanie produktu do dziennika
     */
    @Operation(summary = "Log a food entry", description = "Adds a product or recipe to the user's daily food log.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Food logged successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid log data (validation error)", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content)
    })
    @PostMapping("/log")
    public ResponseEntity<Void> logFood(@Valid @RequestBody AddFoodLogDto logDto, Principal principal) {
        dailyFoodLogService.logFood(logDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint Dnia 2: Pobieranie dziennika na konkretny dzień
     */
    @Operation(summary = "Get daily food log", description = "Retrieves all food entries for a specific date for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DailyLogDto.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content)
    })
    @GetMapping("/log/{date}")
    public ResponseEntity<DailyLogDto> getDailyLog(
            @Parameter(description = "The date to fetch the log for (format: YYYY-MM-DD)", required = true, example = "2025-11-10")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Principal principal) {

        DailyLogDto dailyLog = dailyFoodLogService.getDailyLog(date, principal.getName());
        return ResponseEntity.ok(dailyLog);
    }

    /**
     * Endpoint Dnia 2: Usuwanie wpisu z dziennika
     */
    @Operation(summary = "Delete a food log entry", description = "Removes a specific food entry from the user's log.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entry deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this entry", content = @Content),
            @ApiResponse(responseCode = "404", description = "Log entry not found", content = @Content)
    })
    @DeleteMapping("/log/{logEntryId}")
    public ResponseEntity<Void> deleteFoodLog(
            @Parameter(description = "The ID of the log entry to delete", required = true, example = "123")
            @PathVariable Long logEntryId,
            Principal principal) {

        dailyFoodLogService.deleteFoodLog(logEntryId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
