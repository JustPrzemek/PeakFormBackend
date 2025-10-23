package com.peakform.trainings.trainingsessions.mapper;

import com.peakform.trainings.exerciselogs.dto.ExerciseLogDto;
import com.peakform.trainings.exerciselogs.model.ExerciseLogs;
import com.peakform.trainings.trainingsessions.dto.SpecificSessionWithLogsDto;
import com.peakform.trainings.trainingsessions.dto.TrainingSessionDto;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingSessionMapper {

    @Mapping(source = "exercises.id", target = "exerciseId")
    @Mapping(source = "exercises.name", target = "exerciseName")
    ExerciseLogDto toExerciseLogDto(ExerciseLogs log);

    List<ExerciseLogDto> toExerciseLogDtoList(List<ExerciseLogs> logs);

    @Mapping(source = "workoutPlans.id", target = "planId")
    @Mapping(source = "workoutPlans.name", target = "planName")
    @Mapping(source = "logs", target = "logs")
    TrainingSessionDto toTrainingSessionDto(TrainingSessions session);


    @Mapping(source = "id", target = "sessionId")
    @Mapping(source = "workoutPlans.name", target = "planName")
    @Mapping(source = "logs", target = "excerciseLogsList")
    SpecificSessionWithLogsDto toSpecificSessionWithLogsDto(TrainingSessions session);

}
