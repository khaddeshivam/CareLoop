package com.example.hospitalManagement.repository;

import com.example.hospitalManagement.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
