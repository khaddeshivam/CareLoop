package com.example.hospitalManagement.services;

import com.example.hospitalManagement.dto.AppointmentResponseDto;
import com.example.hospitalManagement.dto.CreateAppointmentRequestDto;
import com.example.hospitalManagement.dto.DoctorResponseDto;
import com.example.hospitalManagement.entities.*;
import com.example.hospitalManagement.repository.AppointmentRepository;
import com.example.hospitalManagement.repository.DoctorRepository;
import com.example.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Appointment createNewAppointment(Appointment appointment, Long doctor_id, Long patient_id) {

        Doctor doctor = doctorRepository.findById(doctor_id).orElseThrow();
        Patient patient = patientRepository.findById(patient_id).orElseThrow();

        if (appointment.getId() != null) throw new IllegalArgumentException("Appointment should not have ID");

        // As in Appointment.java both Patient & Doctor Owns while Appointment is child so we need save this newly created appointment
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        patient.getAppointments().add(appointment); // To maintain Bi-directional consistency

        return appointmentRepository.save(appointment);
    }

//    @Transactional
//    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {
//        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
//        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
//
//        appointment.setDoctor(doctor); // this will automatically will call update, because it is dirty
//
//        doctor.getAppointments().add(appointment); // just for Bi-directional consistency
//
//        return appointment;
//    }


    @Transactional
    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        Doctor newDoctor = doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        Doctor oldDoctor = appointment.getDoctor();

        // Avoid unnecessary reassignment
        if (oldDoctor.getId().equals(newDoctor.getId())) {
            return appointment;
        }

        // Remove appointment from old doctor's collection
        // this will automatically will call update, because it is dirty
        oldDoctor.getAppointments().remove(appointment);

        // Update the owning side of the relationship
        appointment.setDoctor(newDoctor);

        // Maintain the new doctor's collection
        // just for Bi-directional consistency
        newDoctor.getAppointments().add(appointment);

        return appointment;
    }

    @Transactional
    public List<AppointmentResponseDto> getAllAppointmentsOfDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Doctor not found with id " + doctorId));

        return doctor.getAppointments().stream().map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class)).collect(Collectors.toList());
    }


    @Transactional
    public AppointmentResponseDto createAppointmentForAuthenticatedPatient(CreateAppointmentRequestDto request, User authenticatedUser) {
        LocalDateTime appointmentTime = request.getAppointmentTime();

        // 1. Find the requested doctor
        Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        // 2. Resolve patient from authenticated user
        Patient patient = patientRepository.findByUser_id(authenticatedUser.getId()).orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));

        // 3. Reject past appointments
        if (!appointmentTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time must be in the future.");
        }

        // 4. Calculate the 30-minute conflict window
        LocalDateTime requestedStartTime = appointmentTime.minusMinutes(30);
        LocalDateTime requestedEndTime = appointmentTime.plusMinutes(30);

        // 5. Check whether the doctor already has an overlapping appointment
        boolean hasConflict = appointmentRepository.existsByDoctor_IdAndAppointmentTimeLessThanAndAppointmentTimeGreaterThan(doctor.getId(), requestedEndTime, requestedStartTime);
        if (hasConflict) {
            throw new IllegalArgumentException("Doctor already has an appointment during this time slot.");
        }

        // 6. Create appointment
        Appointment appointment = Appointment.builder().appointmentTime(request.getAppointmentTime()).reason(request.getReason()).doctor(doctor).patient(patient).status(AppointmentStatus.SCHEDULED).build();

        // 7. Maintain bidirectional relationship
        patient.getAppointments().add(appointment);

        // 8. Persist appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 9. Build response DTO
        AppointmentResponseDto response = new AppointmentResponseDto();
        response.setId(savedAppointment.getId());
        response.setAppointmentTime(savedAppointment.getAppointmentTime());
        response.setReason(savedAppointment.getReason());

        DoctorResponseDto doctorResponse = new DoctorResponseDto();
        doctorResponse.setId(doctor.getId());
        doctorResponse.setName(doctor.getName());
        doctorResponse.setSpecialization(doctor.getSpecialization());
        doctorResponse.setEmail(doctor.getEmail());

        response.setDoctor(doctorResponse);
        return response;
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, User authenticatedUser) {
        // 1. Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        // 2. Verify ownership
        Long appointmentPatientUserId = appointment.getPatient().getUser().getId();

        if (!appointmentPatientUserId.equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("You cannot cancel another patient's appointment.");
        }

        // 3. Only scheduled appointments can be cancelled
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled appointments can be cancelled.");
        }

        // 4. Update status
        appointment.setStatus(AppointmentStatus.CANCELLED);
    }
}
