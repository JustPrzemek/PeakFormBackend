package com.peakform.trainings.trainingsessions.mapper;

import com.peakform.trainings.trainingsessions.dto.AllTrainingSessionsDto;
import com.peakform.trainings.trainingsessions.model.TrainingSessions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AllTrainingSessionMapper {

    @Mapping(source = "id", target = "sessionId")
    @Mapping(source = "workoutPlans.name", target = "planName")
    AllTrainingSessionsDto toAllTrainingSessionsDto(TrainingSessions session);

}
