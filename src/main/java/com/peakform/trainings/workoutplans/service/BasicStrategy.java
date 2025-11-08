package com.peakform.trainings.workoutplans.service;

import com.peakform.security.user.model.User;
import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.exercises.repository.ExercisesRepository;
import com.peakform.trainings.workoutplanexercises.model.WorkoutPlanExercises;
import com.peakform.trainings.workoutplanexercises.repository.WorkoutPlanExerciseRepository;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import com.peakform.trainings.workoutplans.repository.WorkoutPlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BasicStrategy {

    private final WorkoutPlansRepository workoutPlanRepository;
    private final ExercisesRepository exercisesRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;

//    @Override
//    public boolean supports(PlanGenerationRequestDto request) {
//        // Ta strategia jest dla początkujących na redukcji, którzy chcą ćwiczyć 2 razy w tygodniu
//        return "beginner".equalsIgnoreCase(request.getExperience()) &&
//                "reduction".equalsIgnoreCase(request.getGoal()) &&
//                request.getDaysPerWeek() == 2;
//    }

    public WorkoutPlans generatePlan(User user) {
        WorkoutPlans plan = new WorkoutPlans();
        plan.setUser(user);
        plan.setName("Basic Plan - Feel free to edit");
        plan.setDescription("You can add days, exercises, change description and plan name and more");
        plan.setCreatedAt(LocalDateTime.now());
        workoutPlanRepository.save(plan);

        createTrainingDay(plan, "A");
        createTrainingDay(plan, "B");

        return plan;
    }

    private void createTrainingDay(WorkoutPlans plan, String dayIdentifier) {
        addStrengthExercise(plan, dayIdentifier, "Pompki");
        addStrengthExercise(plan, dayIdentifier, "Wyciskanie sztangi na ławce płaskiej");
        addStrengthExercise(plan, dayIdentifier, "Rozpiętki z hantlami");

        addCardioExercise(plan, dayIdentifier);
    }

    private Exercises getExerciseByName(String exerciseName) {
        return exercisesRepository.findByName(exerciseName)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia: " + exerciseName));
    }

    private void addStrengthExercise(WorkoutPlans plan, String day, String exerciseName) {

        WorkoutPlanExercises wpe = new WorkoutPlanExercises();
        wpe.setWorkoutPlans(plan);
        wpe.setExercises(getExerciseByName(exerciseName));
        wpe.setDayIdentifier(day);
        wpe.setSets(3);
        wpe.setReps(12);
        wpe.setRestTime(90);

        workoutPlanExerciseRepository.save(wpe);
    }

    private void addCardioExercise(WorkoutPlans plan, String day) {

        WorkoutPlanExercises wpe = new WorkoutPlanExercises();
        wpe.setWorkoutPlans(plan);
        wpe.setExercises(getExerciseByName("Jazda na rowerze stacjonarnym"));
        wpe.setDayIdentifier(day);
        wpe.setDurationMinutes(40);
        wpe.setDistanceKm((float) 20);

        workoutPlanExerciseRepository.save(wpe);
    }
}
