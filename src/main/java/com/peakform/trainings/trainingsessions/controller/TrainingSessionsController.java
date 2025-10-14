package com.peakform.trainings.trainingsessions.controller;

import com.peakform.trainings.exerciselogs.dto.ExerciseLogDto;
import com.peakform.trainings.exerciselogs.dto.ExerciseLogRequestDto;
import com.peakform.trainings.trainingsessions.dto.ActiveSessionDto;
import com.peakform.trainings.trainingsessions.dto.BulkLogRequestDto;
import com.peakform.trainings.trainingsessions.dto.TrainingDayDto;
import com.peakform.trainings.trainingsessions.dto.TrainingDaySpecificationDto;
import com.peakform.trainings.trainingsessions.dto.TrainingSessionDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/trainings")
@Tag(name = "Trainings Sessions", description = "Endpoints for managing training sessions and logging exercises")
public interface TrainingSessionsController {

    @GetMapping("/workout-plans/active/days")
    ResponseEntity<List<TrainingDaySpecificationDto>> getActivePlanDays(@AuthenticationPrincipal UserDetails userDetails);

    @PostMapping("/sessions/get-or-create/{dayIdentifier}")
    ResponseEntity<TrainingDayDto> getOrCreateTrainingSession(
            @PathVariable String dayIdentifier,
            @AuthenticationPrincipal UserDetails userDetails);

    @PostMapping("/sessions/{sessionId}/finish")
    ResponseEntity<TrainingSessionDto> finishTrainingSession(@PathVariable Long sessionId,
                                                             @AuthenticationPrincipal UserDetails userDetails);

    @PostMapping("/sessions/{sessionId}/logs")
    ResponseEntity<ExerciseLogDto> addExerciseLog(
            @PathVariable Long sessionId,
            @RequestBody ExerciseLogRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails);

    @DeleteMapping("/sessions/{sessionId}")
    ResponseEntity<Void> deleteTrainingSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails);

    @PostMapping("/sessions/bulk-log")
    ResponseEntity<TrainingSessionDto> logPastWorkout(
            @RequestBody @Valid BulkLogRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails);

    @GetMapping("/sessions/active")
    ResponseEntity<ActiveSessionDto> getActiveSession(
            @AuthenticationPrincipal UserDetails userDetails);
}