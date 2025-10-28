package com.peakform.trainings.trainingsessions.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import com.peakform.trainings.exerciselogs.dto.ExerciseLogDto;
import com.peakform.trainings.exerciselogs.dto.ExerciseLogRequestDto;
import com.peakform.trainings.trainingsessions.dto.ActiveSessionDto;
import com.peakform.trainings.trainingsessions.dto.AllTrainingSessionsDto;
import com.peakform.trainings.trainingsessions.dto.SpecificSessionWithLogsDto;
import com.peakform.trainings.trainingsessions.dto.BulkLogRequestDto;
import com.peakform.trainings.trainingsessions.dto.TrainingDayDto;
import com.peakform.trainings.trainingsessions.dto.TrainingDaySpecificationDto;
import com.peakform.trainings.trainingsessions.dto.TrainingSessionDto;
import com.peakform.trainings.trainingsessions.dto.UpdateSessionRequestDto;
import com.peakform.trainings.trainingsessions.service.TrainingSessionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingSessionsControllerImpl implements TrainingSessionsController {

    private final TrainingSessionsService trainingService;
    private final UserRepository userRepository;

    // Helper do pobierania encji User na podstawie UserDetails z security
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public ResponseEntity<List<TrainingDaySpecificationDto>> getActivePlanDays(UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        List<TrainingDaySpecificationDto> days = trainingService.getActivePlanDays(currentUser);
        return ResponseEntity.ok(days);
    }

    @Override
    public ResponseEntity<TrainingDayDto> getOrCreateTrainingSession(
            String dayIdentifier,
            UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        TrainingDayDto trainingState = trainingService.getOrCreateActiveSessionForDay(currentUser, dayIdentifier);
        return ResponseEntity.ok(trainingState);
    }

    @Override
    public ResponseEntity<Void> deleteTrainingSession(
            Long sessionId,
            UserDetails userDetails) {

        User currentUser = getCurrentUser(userDetails);
        trainingService.deleteSession(sessionId, currentUser);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TrainingSessionDto> finishTrainingSession(Long sessionId, UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        TrainingSessionDto sessionDto = trainingService.finishSession(sessionId, currentUser);
        return ResponseEntity.ok(sessionDto);
    }

    @Override
    public ResponseEntity<ExerciseLogDto> addExerciseLog(Long sessionId, ExerciseLogRequestDto requestDto, UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        ExerciseLogDto logDto = trainingService.addExerciseLog(sessionId, requestDto, currentUser);
        return ResponseEntity.ok(logDto);
    }

    @Override
    public ResponseEntity<TrainingSessionDto> logPastWorkout(
            BulkLogRequestDto requestDto,
            UserDetails userDetails) {

        User currentUser = getCurrentUser(userDetails);
        TrainingSessionDto createdSession = trainingService.logPastWorkout(requestDto, currentUser);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ActiveSessionDto> getActiveSession(
            UserDetails userDetails) {

        User currentUser = getCurrentUser(userDetails);

        return trainingService.findActiveSession(currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Override
    public ResponseEntity<SpecificSessionWithLogsDto> getLastSessionWithLogs(UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(trainingService.getLastSessionWithLogs(currentUser));
    }

    @Override
    public ResponseEntity<SpecificSessionWithLogsDto> getSpecificSessionForUser(UserDetails userDetails, Long sessionId) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(trainingService.getSpecificSessionForUser(currentUser, sessionId));
    }

    @Override
    public ResponseEntity<PagedResponse<AllTrainingSessionsDto>> getAllTrainingSessions(
            UserDetails userDetails,
            String searchParameter,
            Pageable pageable) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(trainingService.getAllTrainingSessions(currentUser, searchParameter, pageable));
    }

    @Override
    public ResponseEntity<SpecificSessionWithLogsDto> updateTrainingSession(
            UserDetails userDetails,
            Long sessionId,
            UpdateSessionRequestDto updateDto) {

        SpecificSessionWithLogsDto updatedSessionDto = trainingService.updateTrainingSession(
                userDetails,
                sessionId,
                updateDto
        );
        return ResponseEntity.ok(updatedSessionDto);
    }
}
