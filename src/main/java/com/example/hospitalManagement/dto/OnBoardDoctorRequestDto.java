package com.example.hospitalManagement.dto;

import lombok.Data;

@Data
public class OnBoardDoctorRequestDto {
    private Long userId;
    private String specialization;
    private String name;
}