package com.peakform.trainings.trainingsessions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDaySpecificationDto {
    private String key;
    private String name;
    private String focus;
}
