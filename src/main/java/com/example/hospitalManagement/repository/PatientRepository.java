package com.example.hospitalManagement.repository;

import com.example.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.entities.type.BloodGroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Patient findByFirstNameAndLastName(String firstName, String lastName);

    List<Patient> findByDateOfBirth(LocalDate dateOfBirth);

    @Query("SELECT p FROM Patient p where  p.bloodGroup = :bloodGroup")
    List<Patient> findBloodGroup(@Param("bloodGroup") BloodGroupType bloodGroupType);

    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth > :dateOfBirth")
    List<Patient> findByDateOfBirthAfter(@Param("dateOfBirth") LocalDate dateOfBirth);

//    @Query("select p.bloodGroup," + " Count(p) from Patient p group by p.bloodGroup")
//    List<Object[]> countEachBloodGroupType();

//    @Query(value = "SELECT * FROM Patients", nativeQuery = true)
//    List<Patient> findAllPatients();

    @Modifying
    @Transactional
    @Query("UPDATE Patient p SET p.firstName = :name where p.id = :id")
    int updateFirstNameWithId(@Param("name") String name, @Param("id") Integer id);

    // Projection in JPQL
    @Query("SELECT new com.example.hospitalManagement.dto.BloodGroupCountResponseEntity(p.bloodGroup, Count(p)) FROM Patient p GROUP BY p.bloodGroup")
    List<BloodGroupCountResponseEntity> countEachBloodGroupType();

    // Pageable
    @Query(value = "SELECT * FROM Patients", nativeQuery = true)
    Page<Patient> findAllPatients(Pageable pageable);

    // N+1 Optimization for patients with there all appointments
    //    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments a LEFT JOIN FETCH a.doctor")
    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments")
    List<Patient> findAllPatientWithAppointment();
}
