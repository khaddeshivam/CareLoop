package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.Appointment;
import com.example.hospitalManagement.entities.Doctor;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.repository.AppointmentRepository;
import com.example.hospitalManagement.repository.DoctorRepository;
import com.example.hospitalManagement.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public Appointment createNewAppointment(Appointment appointment, int doctor_id, int patient_id) {

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
    public Appointment reAssignAppointmentToAnotherDoctor(Long appointmentId, int doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        appointment.setDoctor(doctor); // this will automatically will call update, because it is dirty

        doctor.getAppointments().add(appointment); // just for Bi-directional consistency

        return appointment;
    }

}
