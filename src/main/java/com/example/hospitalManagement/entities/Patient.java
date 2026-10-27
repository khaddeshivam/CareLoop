package com.example.hospitalManagement.entities;

import com.example.hospitalManagement.entities.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@ToString
//@Table(
//        name = "patients",
//        uniqueConstraints = {
//                @UniqueConstraint(name = "unique_patient_name", columnNames = {"lastName"}),
//                @UniqueConstraint(name = "unique_patient_name_birthdate", columnNames = {"lastName", "dateOfBirth"})
//        },
//        indexes = {
//                @Index(name = "idx_patient_birth_date", columnList = "dateOfBirth")
//        }
//)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id") // Maps Java field to PostgreSQL 'patient_id'
    private int id;              // Matches your primary key type

    @Column(name = "first_name")
    private String firstName;    // CamelCase maps automatically to snake_case

    @Column(name = "last_name")
    private String lastName;

//    @ToString.Exclude()
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ToString.Exclude
    @Column(name = "gender")
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false, length = 30)
    private BloodGroupType bloodGroup;  // Changed from String to Enum type

    @Column(name = "phone")      // CRITICAL FIX: Directs Hibernate to read 'phone' instead of 'phone_number'
    private String phoneNumber;  // Stored as String to avoid leading-zero drops or data overflow

//    @Column(name = "insurance_id")
//    private Integer insuranceId;

//    @CreationTimestamp
//    @Column(updatable = false)
//    private LocalDateTime createdAt;

    // Spring Data JPA Mapping
    // Removal Orphan/Cascade
//    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}) // Parent side
    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "patient_insurance_id") // owning side
    private Insurance insurance;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
//    @ToString.Exclude
    private List<Appointment> appointments = new ArrayList<>();
}
