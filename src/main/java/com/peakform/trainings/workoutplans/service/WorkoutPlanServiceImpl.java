package com.peakform.trainings.workoutplans.service;

import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.exercises.repository.ExercisesRepository;
import com.peakform.trainings.workoutplanexercises.model.WorkoutPlaneExercises;
import com.peakform.trainings.workoutplanexercises.repository.WorkoutPlanExerciseRepository;
import com.peakform.trainings.workoutplans.dto.AddExerciseToPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.CreateWorkoutPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.ExerciseInPlanDto;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.dto.UpdateExerciseInPlanRequestDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanDetailDto;
import com.peakform.trainings.workoutplans.dto.WorkoutPlanSummaryDto;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import com.peakform.trainings.workoutplans.repository.WorkoutPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final UserRepository userRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final ExercisesRepository exercisesRepository;
    private final List<PlanGenerationStrategy> generationStrategies;

    @Override
    @Transactional
    public WorkoutPlanDetailDto generatePlan(PlanGenerationRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PlanGenerationStrategy strategy = generationStrategies.stream()
                .filter(s -> s.supports(requestDto))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono strategii dla podanych parametrów."));

        WorkoutPlans newPlan = strategy.generatePlan(user, requestDto);

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

        WorkoutPlaneExercises wpe = new WorkoutPlaneExercises();
        wpe.setWorkoutPlans(plan);
        wpe.setExercises(exercise);
        wpe.setDayIdentifier(requestDto.getDayIdentifier());
        wpe.setSets(requestDto.getSets());
        wpe.setReps(requestDto.getReps());
        wpe.setRestTime(requestDto.getRestTime());

        workoutPlanExerciseRepository.save(wpe);

        return getPlanDetails(planId);
    }

    @Override
    public List<WorkoutPlanSummaryDto> getUserPlans() {
        User user = getCurrentUser();
        List<WorkoutPlans> plans = workoutPlanRepository.findByUserId(user.getId());

        return plans.stream().map(plan -> new WorkoutPlanSummaryDto(
                plan.getId(),
                plan.getName(),
                plan.getCreatedAt(),
                user.getActiveWorkoutPlan() != null && user.getActiveWorkoutPlan().getId().equals(plan.getId())
        )).collect(Collectors.toList());
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
        // Sprawdzamy, czy zarówno plan, jak i ćwiczenie w planie należą do użytkownika
        WorkoutPlans plan = findPlanByIdAndCheckOwnership(planId, user);

        WorkoutPlaneExercises exerciseInPlan = workoutPlanExerciseRepository.findById(workoutPlanExerciseId)
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

        WorkoutPlaneExercises exerciseInPlan = workoutPlanExerciseRepository.findById(workoutPlanExerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise in plan not found with id: " + workoutPlanExerciseId));

        // Sprawdzenie, czy edytowane ćwiczenie na pewno należy do tego użytkownika (przez plan)
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

        List<WorkoutPlaneExercises> exercisesInPlan = workoutPlanExerciseRepository.findByWorkoutPlansId(planId);

        Map<String, List<ExerciseInPlanDto>> days = exercisesInPlan.stream()
                .collect(Collectors.groupingBy(
                        WorkoutPlaneExercises::getDayIdentifier,
                        Collectors.mapping(
                                wpe -> new ExerciseInPlanDto(
                                        wpe.getId(),
                                        wpe.getExercises().getId(),
                                        wpe.getExercises().getName(),
                                        wpe.getSets(),
                                        wpe.getReps(),
                                        wpe.getRestTime()
                                ),
                                Collectors.toList()
                        )
                ));

        boolean activePlan = user.getActiveWorkoutPlan() != null && user.getActiveWorkoutPlan().getId().equals(planId);
        WorkoutPlanDetailDto planDetailsDto = new WorkoutPlanDetailDto();
        planDetailsDto.setId(plan.getId());
        planDetailsDto.setName(plan.getName());
        planDetailsDto.setDays(days);
        planDetailsDto.setActive(activePlan);

        return planDetailsDto;
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
