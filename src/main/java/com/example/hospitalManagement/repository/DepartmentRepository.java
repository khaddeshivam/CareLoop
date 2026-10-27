package com.example.hospitalManagement.repository;

import com.example.hospitalManagement.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
