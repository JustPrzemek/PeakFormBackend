package com.peakform.trainings.trainingsessions.service;

import com.peakform.pages.PagedResponse;
import com.peakform.security.user.model.User;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface TrainingSessionsService {

    TrainingDayDto getOrCreateActiveSessionForDay(User user, String dayIdentifier);

    TrainingSessionDto finishSession(Long sessionId, User user);

    ExerciseLogDto addExerciseLog(Long sessionId, ExerciseLogRequestDto requestDto, User user);

    List<TrainingDaySpecificationDto> getActivePlanDays(User user);

    void deleteSession(Long sessionId, User user);

    TrainingSessionDto logPastWorkout(BulkLogRequestDto request, User user);

    Optional<ActiveSessionDto> findActiveSession(User user);

    SpecificSessionWithLogsDto getLastSessionWithLogs(User currentUser);

    SpecificSessionWithLogsDto getSpecificSessionForUser(User currentUser, Long sessionId);

    PagedResponse<AllTrainingSessionsDto> getAllTrainingSessions(User currentUser, String searchParameter, Pageable pageable);

    SpecificSessionWithLogsDto updateTrainingSession(UserDetails userDetails, Long sessionId, UpdateSessionRequestDto updateDto);
}
