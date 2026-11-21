package com.example.hospitalManagement.repository;

import com.example.hospitalManagement.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctor_IdAndAppointmentTimeLessThanAndAppointmentTimeGreaterThan(
            Long id,
            LocalDateTime requestedEndTime,
            LocalDateTime requestedStartTime
    );
}
