package com.example.hospitalManagement.services;

import com.example.hospitalManagement.dto.AppointmentResponseDto;
import com.example.hospitalManagement.dto.CreateAppointmentRequestDto;
import com.example.hospitalManagement.dto.DoctorResponseDto;
import com.example.hospitalManagement.entities.Appointment;
import com.example.hospitalManagement.entities.Doctor;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.repository.AppointmentRepository;
import com.example.hospitalManagement.repository.DoctorRepository;
import com.example.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    @Transactional
    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        appointment.setDoctor(doctor); // this will automatically will call update, because it is dirty

        doctor.getAppointments().add(appointment); // just for Bi-directional consistency

        return appointment;
    }

    @Transactional
    public List<AppointmentResponseDto> getAllAppointmentsOfDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        return doctor.getAppointments().stream().map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class)).collect(Collectors.toList());
    }


    @Transactional
    public AppointmentResponseDto createAppointmentForAuthenticatedPatient(CreateAppointmentRequestDto request, User authenticatedUser) {

        // 1. Find the requested doctor
        Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        // 2. Resolve patient from authenticated user
        Patient patient = patientRepository.findByUser_id(authenticatedUser.getId()).orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));

        // 3. Create appointment
        Appointment appointment = Appointment
                .builder()
                .appointmentTime(request.getAppointmentTime()).reason(request.getReason())
                .doctor(doctor)
                .patient(patient)
                .build();

        // 4. Maintain bidirectional relationship
        patient.getAppointments().add(appointment);

        // 5. Persist appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 6. Build response DTO
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

}
