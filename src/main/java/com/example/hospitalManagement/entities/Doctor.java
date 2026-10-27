package com.example.hospitalManagement.entities;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
//
//    @OneToOne
//    @MapsId
//    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String specialization;

    @Column(unique = true, length = 100)
    private String email;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;

    @ManyToMany
    private Set<Doctor> doctors = new HashSet<>();
}
