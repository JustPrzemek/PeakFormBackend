package com.peakform.trainings.workoutplans.repository;

import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class WorkoutPlanSpecifications {

    /**
     * Zawsze filtrujemy po ID zalogowanego użytkownika.
     * To jest nasza bazowa specyfikacja.
     */
    public static Specification<WorkoutPlans> withUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
        // Założenie: Encja WorkoutPlans ma pole 'user' typu User.
        // Jeśli masz tam tylko 'userId' typu Long, użyj:
        // return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    /**
     * Filtr na polu 'name' (ignoruje wielkość liter).
     */
    public static Specification<WorkoutPlans> withName(String name) {
        if (!StringUtils.hasText(name)) {
            return null; // Nie dodawaj tego filtra, jeśli 'name' jest puste
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    /**
     * Filtr na polu 'goal' (ignoruje wielkość liter).
     */
    public static Specification<WorkoutPlans> withGoal(String goal) {
        if (!StringUtils.hasText(goal)) {
            return null; // Nie dodawaj tego filtra
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("goal")), "%" + goal.toLowerCase() + "%");
    }

    /**
     * Specjalny filtr dla 'isActive'.
     * @param isActive Wartość filtra (true/false)
     * @param activePlanId ID aktywnego planu użytkownika (może być null)
     */
    public static Specification<WorkoutPlans> withIsActive(Boolean isActive, Long activePlanId) {
        if (isActive == null) {
            return null; // Nie filtruj po aktywności
        }

        return (root, query, cb) -> {
            if (isActive) {
                // Użytkownik chce *tylko* aktywny plan
                if (activePlanId == null) {
                    return cb.disjunction(); // Zwraca "fałsz" - nic nie jest aktywne, więc nic nie pasuje
                }
                return cb.equal(root.get("id"), activePlanId);
            } else {
                // Użytkownik chce *tylko nieaktywne* plany
                if (activePlanId == null) {
                    return cb.conjunction(); // Zwraca "prawda" - nic nie jest aktywne, więc wszystkie są "nieaktywne"
                }
                return cb.notEqual(root.get("id"), activePlanId);
            }
        };
    }
}
