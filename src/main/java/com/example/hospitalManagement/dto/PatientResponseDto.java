package com.example.hospitalManagement.dto;

import com.example.hospitalManagement.entities.type.BloodGroupType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientResponseDto {
    private int id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private BloodGroupType bloodGroup;
}