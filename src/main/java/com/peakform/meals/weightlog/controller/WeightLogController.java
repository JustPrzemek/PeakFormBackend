package com.peakform.meals.weightlog.controller;

import com.peakform.meals.weightlog.dto.WeightHistoryDto;
import com.peakform.meals.weightlog.dto.WeightLogDto;
import com.peakform.meals.weightlog.service.WeightLogService;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition/stats")
@RequiredArgsConstructor
@Tag(name = "Nutrition Stats", description = "Endpoints for tracking weight and other stats")
public class WeightLogController {

    private final WeightLogService weightLogService;

    /**
     * Endpoint Dnia 3: Dodawanie lub aktualizacja wagi na dziś
     */
    @Operation(summary = "Add or update weight", description = "Adds or updates the weight log for the authenticated user for the current date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weight logged successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data (e.g., weight is null)", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content)
    })
    @PostMapping("/weight")
    public ResponseEntity<Void> addOrUpdateWeight(
            @Valid @RequestBody WeightLogDto weightDto,
            Principal principal) {

        // Przekazujemy tylko username, resztą zajmie się serwis
        weightLogService.addOrUpdateWeight(weightDto, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint Dnia 3: Pobieranie historii wagi do wykresu
     */
    @Operation(summary = "Get weight history", description = "Retrieves the user's entire weight history, sorted from newest to oldest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WeightHistoryDto.class)))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content)
    })
    @GetMapping("/weight")
    public ResponseEntity<List<WeightHistoryDto>> getWeightHistory(Principal principal) {
        // Przekazujemy tylko username
        List<WeightHistoryDto> history = weightLogService.getWeightHistory(principal.getName());
        return ResponseEntity.ok(history);
    }
}
