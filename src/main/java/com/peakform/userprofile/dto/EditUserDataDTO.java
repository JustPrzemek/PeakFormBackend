package com.peakform.userprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserDataDTO {
    private String username;
    private String bioTitle;
    private String profileBio;
    private String location;
    private String gender;
    private LocalDate dateOfBirth;
    private Float weight;
    private Float height;
    private String goal;
}
