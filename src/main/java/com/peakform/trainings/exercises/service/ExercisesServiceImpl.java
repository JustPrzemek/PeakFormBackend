package com.peakform.trainings.exercises.service;

import com.peakform.exceptions.ResourceNotFoundException;
import com.peakform.pages.PagedResponse;
import com.peakform.trainings.exercises.dto.ExerciseDto;
import com.peakform.trainings.exercises.dto.SingleExerciseDto;
import com.peakform.trainings.exercises.enums.Difficulty;
import com.peakform.trainings.exercises.mapper.ExerciseMapper;
import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.exercises.repository.ExercisesRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExercisesServiceImpl implements ExercisesService {

    private final ExercisesRepository exercisesRepository;
    private final ExerciseMapper exerciseMapper;

    @Override
    public PagedResponse<ExerciseDto> getExercises(String name, String muscleGroup, Difficulty difficulty, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Exercises> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (muscleGroup != null && !muscleGroup.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("muscleGroup"), muscleGroup));
            }
            if (difficulty != null) {
                predicates.add(criteriaBuilder.equal(root.get("difficulty"), difficulty));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Exercises> exercisesPage = exercisesRepository.findAll(spec, pageable);

        List<ExerciseDto> content = exercisesPage.getContent().stream()
                .map(exerciseMapper::toExerciseDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                exercisesPage.getNumber(),
                exercisesPage.getSize(),
                exercisesPage.getTotalElements(),
                exercisesPage.getTotalPages(),
                exercisesPage.isLast()
        );
    }

    @Override
    public SingleExerciseDto getExerciseById(Long id) {
        Exercises exercise = exercisesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise" + "id" + id + "not found"));
        return exerciseMapper.toSingleExerciseDto(exercise);
    }
}
