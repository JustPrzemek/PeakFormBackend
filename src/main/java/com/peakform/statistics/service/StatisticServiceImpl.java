package com.peakform.statistics.service;

import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import com.peakform.statistics.dto.KpiSummaryDTO;
import com.peakform.statistics.dto.LabelDataPoint;
import com.peakform.statistics.dto.TimeSeriesDataPoint;
import com.peakform.trainings.exerciselogs.repository.ExerciseLogsRepository;
import com.peakform.trainings.trainingsessions.repository.TrainingSessionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final UserRepository userRepository;
    private final TrainingSessionsRepository trainingSessionRepository;
    private final ExerciseLogsRepository exerciseLogRepository;

    @Override
    public KpiSummaryDTO getKpiSummary(LocalDateTime startDate, LocalDateTime endDate) {
        User user = getCurrentUser();
        Long userId = user.getId();

        Long totalWorkouts = Optional.ofNullable(
                trainingSessionRepository.findWorkoutCountInPeriod(userId, startDate, endDate)
        ).orElse(0L);

        Long totalDuration = Optional.ofNullable(
                trainingSessionRepository.findTotalDurationInPeriod(userId, startDate, endDate)
        ).orElse(0L);

        Double totalVolume = Optional.ofNullable(
                exerciseLogRepository.findTotalVolumeInPeriod(userId, startDate, endDate)
        ).orElse(0.0);

        Long totalSets = Optional.ofNullable(
                exerciseLogRepository.findTotalSetsInPeriod(userId, startDate, endDate)
        ).orElse(0L);

        Double totalDistance = Optional.ofNullable(
                exerciseLogRepository.findTotalCardioDistanceInPeriod(userId, startDate, endDate)
        ).orElse(0.0);

        return new KpiSummaryDTO(totalWorkouts, totalDuration, totalVolume, totalSets, totalDistance);
    }

    @Override
    public List<TimeSeriesDataPoint<Long>> getWorkoutFrequency(LocalDateTime startDate, LocalDateTime endDate) {
        User user = getCurrentUser();
        List<Object[]> results = trainingSessionRepository.findWorkoutFrequency(user.getId(), startDate, endDate);

        return results.stream()
                .map(row -> new TimeSeriesDataPoint<>(
                        ((java.sql.Date) row[0]).toLocalDate(), // [0] to 'day'
                        (Long) row[1]                          // [1] to 'count'
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesDataPoint<Double>> getTotalVolumePerDay(LocalDateTime startDate, LocalDateTime endDate) {
        User user = getCurrentUser();
        List<Object[]> results = exerciseLogRepository.findTotalVolumePerDay(user.getId(), startDate, endDate);

        return results.stream()
                .map(row -> new TimeSeriesDataPoint<>(
                        ((java.sql.Date) row[0]).toLocalDate(), // [0] to 'day'
                        (Double) row[1]                        // [1] to 'totalVolume'
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesDataPoint<Double>> getCardioDistancePerDay(LocalDateTime startDate, LocalDateTime endDate) {
        User user = getCurrentUser();
        List<Object[]> results = exerciseLogRepository.findCardioDistancePerDay(user.getId(), startDate, endDate);

        return results.stream()
                .map(row -> new TimeSeriesDataPoint<>(
                        ((java.sql.Date) row[0]).toLocalDate(), // [0] to 'day'
                        (Double) row[1]                        // [1] to 'totalDistance'
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<LabelDataPoint<Long>> getMuscleGroupDistribution(LocalDateTime startDate, LocalDateTime endDate) {
        User user = getCurrentUser();
        List<Object[]> results = exerciseLogRepository.findMuscleGroupDistribution(user.getId(), startDate, endDate);

        return results.stream()
                .map(row -> new LabelDataPoint<>(
                        (String) row[0], // [0] to 'muscle'
                        (Long) row[1]    // [1] to 'setCount'
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesDataPoint<Double>> getExerciseProgression(Long exerciseId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = getCurrentUser();
        List<Object[]> results = exerciseLogRepository.findExerciseProgression(user.getId(), exerciseId, startDate, endDate);

        return results.stream()
                .map(row -> new TimeSeriesDataPoint<>(
                        ((java.sql.Date) row[0]).toLocalDate(), // [0] to 'day'
                        ((Float) row[1]).doubleValue()         // [1] to 'maxWeight' (jest Float, konwertujemy na Double)
                ))
                .collect(Collectors.toList());
    }


    // Prywatna metoda pomocnicza do pobierania użytkownika
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
