package com.peakform.trainings.trainingsessions.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateSessionRequestDto {
    private String notes;
    private List<UpdateLogDto> logsToUpdate;
}
