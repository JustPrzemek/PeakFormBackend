package com.peakform.trainings.workoutplans.service;

import com.peakform.security.user.model.User;
import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.exercises.repository.ExercisesRepository;
import com.peakform.trainings.workoutplanexercises.model.WorkoutPlaneExercises;
import com.peakform.trainings.workoutplanexercises.repository.WorkoutPlanExerciseRepository;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import com.peakform.trainings.workoutplans.repository.WorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeginnerReductionStrategy implements PlanGenerationStrategy {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final ExercisesRepository exercisesRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;

    @Override
    public boolean supports(PlanGenerationRequestDto request) {
        // Ta strategia jest dla początkujących na redukcji, którzy chcą ćwiczyć 2 razy w tygodniu
        return "beginner".equalsIgnoreCase(request.getExperience()) &&
                "reduction".equalsIgnoreCase(request.getGoal()) &&
                request.getDaysPerWeek() == 2;
    }

    @Override
    public WorkoutPlans generatePlan(User user, PlanGenerationRequestDto request) {
        WorkoutPlans plan = new WorkoutPlans();
        plan.setUser(user);
        plan.setName("Plan dla początkujących - Redukcja");
        workoutPlanRepository.save(plan);

        // Dzień A: Lekki trening siłowy
        createTrainingDay(plan, "A");
        // Dzień B: Lekki trening siłowy
        createTrainingDay(plan, "B");

        return plan;
    }

    private void createTrainingDay(WorkoutPlans plan, String dayIdentifier) {
        // Dodaj 3-4 podstawowe ćwiczenia na całe ciało
        addExerciseToPlan(plan, dayIdentifier, "Suwnica (wypychanie ciężaru nogami)", 3, 12, 90);
        addExerciseToPlan(plan, dayIdentifier, "Ściąganie drążka wyciągu górnego do klatki", 3, 12, 90);
        addExerciseToPlan(plan, dayIdentifier, "Wyciskanie hantli na ławce płaskiej", 3, 12, 90);

        // Dodaj Cardio na koniec
        addExerciseToPlan(plan, dayIdentifier, "Rowerek stacjonarny", 1, 20, 0); // Reps jako minuty
    }

    private void addExerciseToPlan(WorkoutPlans plan, String day, String exerciseName, int sets, int reps, int rest) {
        Exercises exercise = exercisesRepository.findByName(exerciseName)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono ćwiczenia: " + exerciseName));

        WorkoutPlaneExercises wpe = new WorkoutPlaneExercises();
        wpe.setWorkoutPlans(plan);
        wpe.setExercises(exercise);
        wpe.setDayIdentifier(day);
        wpe.setSets(sets);
        wpe.setReps(reps);
        wpe.setRestTime(rest);
        workoutPlanExerciseRepository.save(wpe);
    }
}
