package com.peakform.trainings.trainingsessions.repository;

import com.peakform.security.user.model.User;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class TrainingSessionsSpecification {

    public static Specification<TrainingSessions> isOwnedAndCompleted(User currentUser) {
        return (root, query, cb) -> {
            Predicate ownerPredicate = cb.equal(root.get("user"), currentUser);
            Predicate completedPredicate = cb.isNotNull(root.get("endTime"));
            return cb.and(ownerPredicate, completedPredicate);
        };
    }

    public static Specification<TrainingSessions> isCompleted() {
        return (root, query, cb) -> cb.isNotNull(root.get("endTime"));
    }

    public static Specification<TrainingSessions> hasSearchParameter(String searchParameter) {
        if (searchParameter == null || searchParameter.trim().isEmpty()) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        }

        String pattern = "%" + searchParameter.toLowerCase() + "%";

        return (root, query, criteriaBuilder) -> {
            Join<TrainingSessions, WorkoutPlans> planJoin = root.join("workoutPlans");

            Predicate planNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(planJoin.get("name")), pattern);

            Predicate dayIdentifierPredicate = criteriaBuilder.like(root.get("dayIdentifier"), pattern);

            return criteriaBuilder.or(planNamePredicate, dayIdentifierPredicate);
        };
    }
}
