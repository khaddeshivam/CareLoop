package com.example.hospitalManagement.controller;

import com.example.hospitalManagement.dto.AppointmentResponseDto;
import com.example.hospitalManagement.dto.CreateAppointmentRequestDto;
import com.example.hospitalManagement.dto.PatientProfileResponseDto;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.services.AppointmentService;
import com.example.hospitalManagement.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

//    @PostMapping("/appointments")
//    public ResponseEntity<AppointmentService> createNewAppointment(
//            @RequestBody CreateAppointmentRequestDto createAppointmentRequestDto) {
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(appointmentService);
//    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponseDto> createNewAppointment(
            @RequestBody CreateAppointmentRequestDto request,
            @AuthenticationPrincipal User authenticatedUser) {

        AppointmentResponseDto response =
                appointmentService.createAppointmentForAuthenticatedPatient(request, authenticatedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal User authenticatedUser) {

        appointmentService.cancelAppointment(appointmentId, authenticatedUser);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<PatientProfileResponseDto> getPatientProfile(@AuthenticationPrincipal User autheticatedUser) {

        return ResponseEntity.ok(patientService.getMyPatientProfile(autheticatedUser));
    }

}