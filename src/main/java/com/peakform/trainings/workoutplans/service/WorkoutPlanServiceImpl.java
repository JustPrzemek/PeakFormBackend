package com.peakform.trainings.workoutplans.service;

import com.peakform.gemini.service.AiGenerationService;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.exercises.repository.ExercisesRepository;
import com.peakform.trainings.workoutplanexercises.dto.PlanExerciseDetailsDto;
import com.peakform.trainings.workoutplanexercises.model.WorkoutPlanExercises;
import com.peakform.trainings.workoutplanexercises.repository.WorkoutPlanExerciseRepository;
import com.peakform.trainings.workoutplans.dto.AddExerciseToPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.CreateWorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.ExerciseInPlanDto;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.dto.UpdateExerciseInPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanDetailDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanSummaryDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanUpdateDto;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import com.peakform.trainings.workoutplans.repository.WorkoutPlanSpecifications;
import com.peakform.trainings.workoutplans.repository.WorkoutPlansRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final UserRepository userRepository;
    private final WorkoutPlansRepository workoutPlanRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final ExercisesRepository exercisesRepository;
//    private final List<PlanGenerationStrategy> generationStrategies;
    private final BasicStrategy basicStrategy;
    private final WorkoutPlanExerciseRepository planExercisesRepository;
    private final AiGenerationService aiGenerationService;

    private static final int MAX_GENERATIONS_PER_DAY = 2;

    @Override
    @Transactional
    public WorkoutPlanDetailDto generateBasicPlan() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        WorkoutPlans newPlan = basicStrategy.generatePlan(user);

        user.setActiveWorkoutPlan(newPlan);
        userRepository.save(user);

        return getPlanDetails(newPlan.getId());
    }

    @Override
    @Transactional
    public WorkoutPlanDetailDto generatePlan(PlanGenerationRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        LocalDate today = LocalDate.now();
        int attemptsToday = user.getGenerationAttemptsToday() != null ? user.getGenerationAttemptsToday() : 0;
        LocalDate lastAttemptDate = user.getLastGenerationAttemptDate();

        if (lastAttemptDate != null && lastAttemptDate.isEqual(today)) {
            // Użytkownik już dziś generował. Sprawdzamy limit.
            if (attemptsToday >= MAX_GENERATIONS_PER_DAY) {
                throw new IllegalStateException(
                        "Przekroczono limit " + MAX_GENERATIONS_PER_DAY + " generacji planu na dziś. Spróbuj ponownie jutro."
                );
            }
            user.setGenerationAttemptsToday(attemptsToday + 1);
        } else {
            // Pierwsza generacja dzisiaj (lub pierwsza w ogóle). Resetujemy licznik.
            user.setLastGenerationAttemptDate(today);
            user.setGenerationAttemptsToday(1);
        }

        // Zapisujemy zaktualizowany stan licznika w bazie
        // Robimy to PRZED wywołaniem AI, aby nawet nieudana próba się liczyła.
        // moze to zmeinie ale narazie tak jest i tam ma zostac nie wiem
        userRepository.save(user);

        WorkoutPlans newPlan = aiGenerationService.generatePlan(user, requestDto);

        if (requestDto.isSetActive()) {
            user.setActiveWorkoutPlan(newPlan);
            userRepository.save(user);
        }

        return getPlanDetails(newPlan.getId());
    }

    @Override
    @Transactional
    public WorkoutPlanDetailDto createEmptyPlan(CreateWorkoutPlanRequestDto requestDto) {
        User user = getCurrentUser();

        WorkoutPlans newPlan = new WorkoutPlans();
        newPlan.setName(requestDto.getName());
        newPlan.setUser(user);
        newPlan.setCreatedAt(LocalDateTime.now());
        newPlan.setDescription(requestDto.getDescription());
        newPlan.setGoal(requestDto.getGoal());

        WorkoutPlans savedPlan = workoutPlanRepository.save(newPlan);

        if (requestDto.isSetActive()) {
            user.setActiveWorkoutPlan(savedPlan);
            userRepository.save(user);
        }

        return getPlanDetails(savedPlan.getId());
    }

    @Override
    @Transactional
    public WorkoutPlanDetailDto addExerciseToPlan(Long planId, AddExerciseToPlanRequestDto requestDto) {
        User user = getCurrentUser();
        WorkoutPlans plan = findPlanByIdAndCheckOwnership(planId, user);

        Exercises exercise = exercisesRepository.findById(requestDto.getExerciseId())
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found with id: " + requestDto.getExerciseId()));

        WorkoutPlanExercises wpe = getWorkoutPlanExercises(requestDto, plan, exercise);

        workoutPlanExerciseRepository.save(wpe);

        return getPlanDetails(planId);
    }

    private static WorkoutPlanExercises getWorkoutPlanExercises(AddExerciseToPlanRequestDto requestDto, WorkoutPlans plan, Exercises exercise) {
        WorkoutPlanExercises wpe = new WorkoutPlanExercises();
        wpe.setWorkoutPlans(plan);
        wpe.setExercises(exercise);
        wpe.setDayIdentifier(requestDto.getDayIdentifier());
        if ("STRENGTH".equalsIgnoreCase(exercise.getType())) {
            wpe.setSets(requestDto.getSets());
            wpe.setReps(requestDto.getReps());
            wpe.setRestTime(requestDto.getRestTime());
        } else if ("CARDIO".equalsIgnoreCase(exercise.getType())) {
            wpe.setDurationMinutes(requestDto.getDurationMinutes());
            wpe.setDistanceKm(requestDto.getDistanceKm());
        }
        return wpe;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkoutPlanSummaryDto> getUserPlans(String name, String goal, Boolean isActive, Pageable pageable) {
        User user = getCurrentUser();

        Optional<Long> activePlanIdOptional = Optional.ofNullable(user.getActiveWorkoutPlan())
                .map(WorkoutPlans::getId);
        Long activePlanId = activePlanIdOptional.orElse(null);

        Specification<WorkoutPlans> spec = WorkoutPlanSpecifications.withUserId(user.getId());
        spec = spec.and(WorkoutPlanSpecifications.withName(name))
                .and(WorkoutPlanSpecifications.withGoal(goal))
                .and(WorkoutPlanSpecifications.withIsActive(isActive, activePlanId));

        Page<WorkoutPlans> plansPage = workoutPlanRepository.findAll(spec, pageable);

        return plansPage.map(plan -> {
            List<String> days = planExercisesRepository.findDistinctDayIdentifiersByWorkoutPlanId(plan.getId());

            return WorkoutPlanSummaryDto.builder()
                    .id(plan.getId())
                    .name(plan.getName())
                    .createdAt(plan.getCreatedAt())
                    .isActive(activePlanIdOptional.map(id -> id.equals(plan.getId())).orElse(false))
                    .days(days)
                    .goal(plan.getGoal())
                    .build();
        });
    }

    @Transactional(readOnly = true)
    public List<PlanExerciseDetailsDto> getExercisesForPlanDay(Long planId, String dayIdentifier) {
        User user = getCurrentUser();
        WorkoutPlans plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + planId));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to access this plan.");
        }

        List<WorkoutPlanExercises> planExercises = planExercisesRepository.findByWorkoutPlansAndDayIdentifier(plan, dayIdentifier);

        return planExercises.stream().map(pe -> PlanExerciseDetailsDto.builder()
                .exerciseId(pe.getExercises().getId())
                .name(pe.getExercises().getName())
                .muscleGroup(pe.getExercises().getMuscleGroup())
                .exerciseType(pe.getExercises().getType())
                .sets(pe.getSets())
                .reps(pe.getReps())
                .durationMinutes(pe.getDurationMinutes())
                .distanceKm(pe.getDistanceKm())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePlan(Long planId) {
        User user = getCurrentUser();
        WorkoutPlans plan = findPlanByIdAndCheckOwnership(planId, user);

        if (user.getActiveWorkoutPlan() != null && user.getActiveWorkoutPlan().getId().equals(planId)) {
            user.setActiveWorkoutPlan(null);
            userRepository.save(user);
        }

        workoutPlanRepository.delete(plan);
    }

    @Override
    @Transactional
    public void removeExerciseFromPlan(Long planId, Long workoutPlanExerciseId) {
        User user = getCurrentUser();
        WorkoutPlans plan = findPlanByIdAndCheckOwnership(planId, user);

        WorkoutPlanExercises exerciseInPlan = workoutPlanExerciseRepository.findById(workoutPlanExerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise in plan not found with id: " + workoutPlanExerciseId));

        if (!exerciseInPlan.getWorkoutPlans().getId().equals(plan.getId())) {
            throw new SecurityException("Exercise does not belong to the specified plan.");
        }

        workoutPlanExerciseRepository.delete(exerciseInPlan);
    }

    @Override
    @Transactional
    public WorkoutPlanDetailDto updateExerciseInPlan(Long planId, Long workoutPlanExerciseId, UpdateExerciseInPlanRequestDto requestDto) {
        User user = getCurrentUser();
        findPlanByIdAndCheckOwnership(planId, user);

        WorkoutPlanExercises exerciseInPlan = workoutPlanExerciseRepository.findById(workoutPlanExerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise in plan not found with id: " + workoutPlanExerciseId));

        // Sprawdzenie, czy edytowane ćwiczenie na pewno należy do tego użytkownika (przez plan) zupdatowac pozniej te
        // exceptiony wszedzie tutaj jest raczej ok
        if (!exerciseInPlan.getWorkoutPlans().getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied to update this resource.");
        }

        exerciseInPlan.setSets(requestDto.getSets());
        exerciseInPlan.setReps(requestDto.getReps());
        exerciseInPlan.setRestTime(requestDto.getRestTime());
        workoutPlanExerciseRepository.save(exerciseInPlan);

        return getPlanDetails(planId);
    }

    @Override
    public WorkoutPlanDetailDto getPlanDetails(Long planId) {
        User user = getCurrentUser();
        WorkoutPlans plan = findPlanByIdAndCheckOwnership(planId, user);

        List<WorkoutPlanExercises> exercisesInPlan = workoutPlanExerciseRepository.findByWorkoutPlansId(planId);

        Map<String, List<ExerciseInPlanDto>> days = exercisesInPlan.stream()
                .collect(Collectors.groupingBy(
                        WorkoutPlanExercises::getDayIdentifier,
                        Collectors.mapping(
                                wpe -> new ExerciseInPlanDto(
                                        wpe.getId(),
                                        wpe.getExercises().getId(),
                                        wpe.getExercises().getName(),
                                        wpe.getExercises().getType(),
                                        wpe.getSets(),
                                        wpe.getReps(),
                                        wpe.getRestTime(),
                                        wpe.getDurationMinutes(),
                                        wpe.getDistanceKm()
                                ),
                                Collectors.toList()
                        )
                ));

        boolean activePlan = user.getActiveWorkoutPlan() != null && user.getActiveWorkoutPlan().getId().equals(planId);
        WorkoutPlanDetailDto planDetailsDto = new WorkoutPlanDetailDto();
        planDetailsDto.setId(plan.getId());
        planDetailsDto.setName(plan.getName());
        planDetailsDto.setDescription(plan.getDescription());
        planDetailsDto.setDays(days);
        planDetailsDto.setActive(activePlan);
        planDetailsDto.setGoal(plan.getGoal());

        return planDetailsDto;
    }

    @Override
    @Transactional
    public WorkoutPlanDetailDto updatePlanDetails(Long planId, WorkoutPlanUpdateDto dto) {
        User user = getCurrentUser();

        WorkoutPlans plan = findPlanByIdAndCheckOwnership(planId, user);

        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setGoal(dto.getGoal());

        workoutPlanRepository.save(plan);

        return getPlanDetails(planId);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private WorkoutPlans findPlanByIdAndCheckOwnership(Long planId, User user) {
        WorkoutPlans plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with id: " + planId));
        if (!plan.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to access this plan.");
        }
        return plan;
    }
}
