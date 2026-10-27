package com.example.hospitalManagement.repository;

import com.example.hospitalManagement.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
}
