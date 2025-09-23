package com.peakform.security.user.repository;

import com.peakform.security.user.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    /**
     * Zwraca specyfikację, która wyszukuje użytkowników po fragmencie nazwy użytkownika.
     * Wyszukiwanie jest case-insensitive (ignoruje wielkość liter).
     * @param searchTerm fragment nazwy użytkownika do wyszukania.
     * @return Specification<User>
     */
    public static Specification<User> searchByUsernameExcludingCurrentUser(String searchTerm, String currentUser) {
        return (root, query, criteriaBuilder) -> {

            // Warunek na wyszukiwanie po nazwie (tak jak wcześniej)
            Predicate searchPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")),
                    "%" + searchTerm.toLowerCase() + "%"
            );

            // Warunek na wykluczenie zalogowanego użytkownika
            Predicate excludeCurrentUserPredicate = criteriaBuilder.notEqual(
                    root.get("username"),
                    currentUser
            );

            // Połącz oba warunki za pomocą AND
            return criteriaBuilder.and(searchPredicate, excludeCurrentUserPredicate);
        };
    }
}