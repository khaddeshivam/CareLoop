package com.example.hospitalManagement.controller;

import com.example.hospitalManagement.dto.CreateAppointmentRequestDto;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.services.AppointmentService;
import com.example.hospitalManagement.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentService> createNewAppointment(
            @RequestBody CreateAppointmentRequestDto createAppointmentRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService);
    }

    @GetMapping("/profile")
    private ResponseEntity<Patient> getPatientProfile() {
        int patientId = 4;
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

}