package com.example.hospitalManagement.dto;

import com.example.hospitalManagement.entities.type.BloodGroupType;
import lombok.*;

import java.time.LocalDate;


@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientProfileResponseDto {

    private int id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private BloodGroupType bloodGroup;
    private String phoneNumber;
}