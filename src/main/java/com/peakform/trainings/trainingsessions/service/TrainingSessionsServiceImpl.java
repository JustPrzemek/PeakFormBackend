package com.peakform.trainings.trainingsessions.service;

import com.peakform.security.user.model.User;
import com.peakform.trainings.exerciselogs.dto.ExerciseLogDto;
import com.peakform.trainings.exerciselogs.dto.ExerciseLogRequestDto;
import com.peakform.trainings.exerciselogs.model.ExerciseLogs;
import com.peakform.trainings.exerciselogs.repository.ExerciseLogsRepository;
import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.exercises.repository.ExercisesRepository;
import com.peakform.trainings.trainingsessions.dto.ActiveSessionDto;
import com.peakform.trainings.trainingsessions.dto.BulkExerciseLogDto;
import com.peakform.trainings.trainingsessions.dto.BulkLogRequestDto;
import com.peakform.trainings.trainingsessions.dto.BulkSetDto;
import com.peakform.trainings.trainingsessions.dto.PlanExerciseDto;
import com.peakform.trainings.trainingsessions.dto.TrainingDayDto;
import com.peakform.trainings.trainingsessions.dto.TrainingDaySpecificationDto;
import com.peakform.trainings.trainingsessions.dto.TrainingSessionDto;
import com.peakform.trainings.trainingsessions.mapper.TrainingSessionMapper;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
import com.peakform.trainings.trainingsessions.repository.TrainingSessionsRepository;
import com.peakform.trainings.workoutplanexercises.model.WorkoutPlanExercises;
import com.peakform.trainings.workoutplanexercises.repository.WorkoutPlanExerciseRepository;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import com.peakform.trainings.workoutplans.repository.WorkoutPlansRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingSessionsServiceImpl implements TrainingSessionsService {

    private final TrainingSessionsRepository sessionsRepository;
    private final ExerciseLogsRepository logsRepository;
    private final ExercisesRepository exercisesRepository;
    private final WorkoutPlansRepository workoutPlansRepository;
    private final TrainingSessionMapper trainingMapper;
    private final WorkoutPlanExerciseRepository planExercisesRepository;

    @Override
    public List<TrainingDaySpecificationDto> getActivePlanDays(User user) {
        WorkoutPlans activePlan = user.getActiveWorkoutPlan();
        if (activePlan == null) {
            throw new RuntimeException("User does not have an active workout plan.");
        }

        List<WorkoutPlanExercises> exercisesInPlan = planExercisesRepository.findAllByWorkoutPlanIdWithExercises(activePlan.getId());


        Map<String, Set<String>> musclesByDay = exercisesInPlan.stream()
                .collect(Collectors.groupingBy(
                        WorkoutPlanExercises::getDayIdentifier,
                        Collectors.mapping(ex -> ex.getExercises().getMuscleGroup(), Collectors.toSet())
                ));

        return musclesByDay.entrySet().stream()
                .map(entry -> {
                    String dayKey = entry.getKey();
                    Set<String> muscleGroups = entry.getValue();

                    String name = formatDayName(dayKey); // Formatujemy nazwę (np. DAY_1 -> Day 1)
                    String focus = String.join(", ", muscleGroups); // Łączymy partie w stringa

                    return new TrainingDaySpecificationDto(dayKey, name, focus);
                })
                .sorted(Comparator.comparing(TrainingDaySpecificationDto::getKey)) // Sortujemy dni rosnąco
                .collect(Collectors.toList());
    }

    private String formatDayName(String dayIdentifier) {
        if (dayIdentifier == null || !dayIdentifier.contains("_")) {
            return dayIdentifier;
        }
        return dayIdentifier.replace("_", " ");
    }

    @Transactional
    public TrainingDayDto getOrCreateActiveSessionForDay(User user, String dayIdentifier) {
        Optional<TrainingSessions> activeSessionOpt = sessionsRepository.findByUserAndEndTimeIsNull(user);

        if (activeSessionOpt.isPresent()) {
            TrainingSessions activeSession = activeSessionOpt.get();

            if (activeSession.getDayIdentifier().equals(dayIdentifier)) {
                return mapToTrainingDayDto(activeSession);
            } else {
                throw new IllegalStateException(
                        "Masz już aktywny trening w dniu " + activeSession.getDayIdentifier() + ". Zakończ go, aby rozpocząć nowy."
                );
            }
        } else {
            WorkoutPlans activePlan = user.getActiveWorkoutPlan();
            if (activePlan == null) {
                throw new IllegalStateException("Nie można rozpocząć sesji bez aktywnego planu treningowego.");
            }

            TrainingSessions newSession = new TrainingSessions();
            newSession.setUser(user);
            newSession.setWorkoutPlans(activePlan);
            newSession.setStartTime(LocalDateTime.now());
            newSession.setDayIdentifier(dayIdentifier);

            TrainingSessions savedSession = sessionsRepository.save(newSession);

            return mapToTrainingDayDto(savedSession);
        }
    }

    private TrainingDayDto mapToTrainingDayDto(TrainingSessions session) {
        WorkoutPlans plan = session.getWorkoutPlans();
        String dayIdentifier = session.getDayIdentifier();
        List<WorkoutPlanExercises> planExercisesForDay = planExercisesRepository.findByWorkoutPlansAndDayIdentifier(plan, dayIdentifier);

        List<PlanExerciseDto> exerciseDtos = planExercisesForDay.stream().map(planExercise -> {
            PlanExerciseDto dto = new PlanExerciseDto();
            dto.setExerciseId(planExercise.getExercises().getId());
            dto.setName(planExercise.getExercises().getName());
            dto.setMuscleGroup(planExercise.getExercises().getMuscleGroup());
            dto.setSets(planExercise.getSets());
            dto.setReps(planExercise.getReps());
            dto.setRestTime(planExercise.getRestTime());
            return dto;
        }).collect(Collectors.toList());

        List<ExerciseLogDto> completedLogs = session.getLogs().stream()
                .map(trainingMapper::toExerciseLogDto)
                .collect(Collectors.toList());

        TrainingDayDto stateDto = new TrainingDayDto();
        stateDto.setSessionId(session.getId());
        stateDto.setPlanName(plan.getName());
        stateDto.setDayIdentifier(dayIdentifier);
        stateDto.setExercises(exerciseDtos);
        stateDto.setCompletedLogs(completedLogs);

        return stateDto;
    }

    @Transactional
    public void deleteSession(Long sessionId, User user) {
        TrainingSessions session = sessionsRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to delete this session.");
        }
        sessionsRepository.delete(session);
    }

    @Transactional
    public TrainingSessionDto finishSession(Long sessionId, User user) {
        TrainingSessions session = sessionsRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found!"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to finish this session.");
        }

        session.setEndTime(LocalDateTime.now());
        sessionsRepository.save(session);
        TrainingSessions updatedSession = sessionsRepository.save(session);

        return trainingMapper.toTrainingSessionDto(updatedSession);
    }

    @Transactional
    public ExerciseLogDto addExerciseLog(Long sessionId, ExerciseLogRequestDto requestDto, User user) {
        TrainingSessions session = sessionsRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found!"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to add logs to this session.");
        }

        Exercises exercise = exercisesRepository.findById(requestDto.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found!"));

        ExerciseLogs newLog = new ExerciseLogs();
        newLog.setTrainingSessions(session);
        newLog.setExercises(exercise);
        newLog.setSetNumber(requestDto.getSetNumber());
        newLog.setReps(requestDto.getReps());
        newLog.setWeight(requestDto.getWeight());
        newLog.setCreatedAt(LocalDateTime.now());

        ExerciseLogs savedLog = logsRepository.save(newLog);
        return trainingMapper.toExerciseLogDto(savedLog);
    }

    @Transactional
    public TrainingSessionDto logPastWorkout(BulkLogRequestDto request, User user) {
        WorkoutPlans plan = workoutPlansRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to log a workout for this plan.");
        }

        TrainingSessions session = new TrainingSessions();
        session.setUser(user);
        session.setWorkoutPlans(plan);
        session.setDayIdentifier(request.getDayIdentifier());
        session.setNotes(request.getNotes());

        LocalDateTime workoutDateTime = request.getWorkoutDate().atStartOfDay();
        session.setStartTime(workoutDateTime);
        session.setEndTime(workoutDateTime);

        TrainingSessions savedSession = sessionsRepository.save(session);

        List<ExerciseLogs> allLogs = new ArrayList<>();

        for (BulkExerciseLogDto exerciseDto : request.getExercises()) {
            Exercises exercise = exercisesRepository.findById(exerciseDto.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found"));

            int setCounter = 1;
            for (BulkSetDto setDto : exerciseDto.getSets()) {
                ExerciseLogs log = new ExerciseLogs();
                log.setTrainingSessions(savedSession);
                log.setExercises(exercise);
                log.setSetNumber(setCounter++);
                log.setReps(setDto.getReps());
                log.setWeight(setDto.getWeight());
                log.setCreatedAt(workoutDateTime);
                allLogs.add(log);
            }
        }

        logsRepository.saveAll(allLogs);

        return trainingMapper.toTrainingSessionDto(savedSession);
    }

    @Transactional(readOnly = true)
    public Optional<ActiveSessionDto> findActiveSession(User user) {
        Optional<TrainingSessions> activeSessionOpt = sessionsRepository.findByUserAndEndTimeIsNull(user);

        return activeSessionOpt.map(session -> ActiveSessionDto.builder()
                .sessionId(session.getId())
                .planName(session.getWorkoutPlans().getName())
                .dayIdentifier(session.getDayIdentifier())
                .build());
    }
}
