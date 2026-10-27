package com.example.hospitalManagement.repository;

import com.example.hospitalManagement.entities.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceRepository extends JpaRepository<Insurance, Integer> {
}
