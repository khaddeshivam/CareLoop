package com.example.hospitalManagement;

import com.example.hospitalManagement.dto.BloodGroupCountResponseEntity;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.entities.type.BloodGroupType;
import com.example.hospitalManagement.services.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.hospitalManagement.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class PatientTest {

    @Autowired
    private PatientRepository patientTestRepository;

    @Autowired
    private PatientService patientService;

    @Test
    public void testPatientRepository() {
        List<Patient> patientList = patientTestRepository.findAll();
        System.out.println(patientList);
//
//        Patient p1 = new Patient();
//        p1.setFirstName("Shub");
//        p1.setLastName("Sharma");
//        p1.setDateOfBirth(LocalDate.of(1990, 5, 12));
//        p1.setGender("Female");
//        p1.setBloodGroup(BloodGroupType.O_POSITIVE);
//        p1.setPhoneNumber("555-4433");
//
//        patientTestRepository.save(p1);
    }

    @Test
    public void testTransactionsMethods() {
//        Patient patient = patientService.getPatientById(13);

        // Spring Data JPA
        // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
        Patient patient = patientTestRepository.findByFirstNameAndLastName("Dinesh ", "Kartik");

        List<Patient> patient1 = patientTestRepository.findByDateOfBirth(LocalDate.of(1990, 5, 12));

        List<Patient> patient2 = patientTestRepository.findBloodGroup(BloodGroupType.O_POSITIVE);

        List<Patient> patient3 = patientTestRepository.findByDateOfBirthAfter(LocalDate.of(1990, 5, 12));

//        List<Object[]> patient4 = patientTestRepository.countEachBloodGroupType();

//        List<Patient> patient5 = patientTestRepository.findAllPatients();

        int rowsUpdated = patientTestRepository.updateFirstNameWithId("Dinesh", 1);

        // Projection in JPQL
        List<BloodGroupCountResponseEntity> bloodGroupList = patientTestRepository.countEachBloodGroupType();

        // Pageable
        Page<Patient> patientList = patientTestRepository.findAllPatients(PageRequest.of(0, 2, Sort.by("patient_id").ascending()));

        // N+1 Optimization for patients with there all appointments
        List<Patient> patientList1 = patientTestRepository.findAllPatientWithAppointment();

//        System.out.println(patient);
//        System.out.println(patient1.stream().toList());
//        System.out.println(patient2);
//        System.out.println(patient3);
//        for (Object[] obj : patient4){
//            System.out.println(obj[0] + " " + obj[1]);
//        }
//        System.out.println(patient5);

//        System.out.println(rowsUpdated);

        // Projection in JPQL
//        for (BloodGroupCountResponseEntity bloodGroupCountResponse : bloodGroupList) {
//            System.out.println(bloodGroupCountResponse);
//        }

        // Pageable
//        for (Patient p : patientList){
//            System.out.println(p);
//        }

        // N+1 optimization
        System.out.println(patientList1);
    }

}
