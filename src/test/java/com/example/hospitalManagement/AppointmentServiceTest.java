package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.Appointment;
import com.example.hospitalManagement.entities.Doctor;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.repository.AppointmentRepository;
import com.example.hospitalManagement.repository.DoctorRepository;
import com.example.hospitalManagement.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                doctorRepository,
                patientRepository,
                appointmentRepository,
                new ModelMapper()
        );
    }

    @Test
    void createNewAppointment_shouldAttachPatientAndDoctorAndSave() {
        Doctor doctor = doctor(3);
        Patient patient = patient(11L);

        Appointment appointment = Appointment.builder()
                .appointmentTime(LocalDateTime.of(2026, 12, 1, 10, 30))
                .reason("Consultation")
                .build();

        when(doctorRepository.findById(3L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(11L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment result =
                appointmentService.createNewAppointment(appointment, 3L, 11L);

        assertSame(appointment, result);
        assertSame(doctor, result.getDoctor());
        assertSame(patient, result.getPatient());
        assertTrue(patient.getAppointments().contains(appointment));

        verify(doctorRepository).findById(3L);
        verify(patientRepository).findById(11L);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void createNewAppointment_shouldRejectAppointmentThatAlreadyHasId() {
        Doctor doctor = doctor(3);
        Patient patient = patient(11L);

        Appointment appointment = Appointment.builder()
                .id(50L)
                .appointmentTime(LocalDateTime.of(2026, 12, 1, 10, 30))
                .reason("Consultation")
                .build();

        when(doctorRepository.findById(3L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(11L)).thenReturn(Optional.of(patient));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createNewAppointment(
                        appointment, 3L, 11L
                )
        );

        assertEquals("Appointment should not have ID", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void reAssignAppointmentToAnotherDoctor_shouldUpdateDoctorRelationship() {
        Doctor previousDoctor = doctor(3);
        Doctor newDoctor = doctor(4);
        Patient patient = patient(11L);

        Appointment appointment = Appointment.builder()
                .id(90L)
                .appointmentTime(LocalDateTime.of(2026, 12, 2, 11, 0))
                .reason("Follow-up")
                .patient(patient)
                .doctor(previousDoctor)
                .build();

        when(appointmentRepository.findById(90L))
                .thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(4L))
                .thenReturn(Optional.of(newDoctor));

        Appointment result =
                appointmentService.reAssignAppointmentToAnotherDoctor(90L, 4L);

        assertSame(appointment, result);
        assertSame(newDoctor, result.getDoctor());
        assertTrue(newDoctor.getAppointments().contains(appointment));

        verify(appointmentRepository).findById(90L);
        verify(doctorRepository).findById(4L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void getAllAppointmentsOfDoctor_shouldReturnEmptyListWhenDoctorHasNoAppointments() {
        Doctor doctor = doctor(3);
        doctor.setAppointments(new ArrayList<>());

        when(doctorRepository.findById(3L)).thenReturn(Optional.of(doctor));

        var result = appointmentService.getAllAppointmentsOfDoctor(3L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository).findById(3L);
    }

    private Doctor doctor(int id) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setName("Doctor " + id);
        doctor.setAppointments(new ArrayList<>());
        return doctor;
    }

    private Patient patient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName("Test");
        patient.setLastName("Patient");
        patient.setAppointments(new ArrayList<>());
        return patient;
    }
}
