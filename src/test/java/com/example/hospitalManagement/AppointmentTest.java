package com.example.hospitalManagement;

import com.example.hospitalManagement.entities.Appointment;
import com.example.hospitalManagement.services.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class AppointmentTest {

    @Autowired
    private AppointmentService appointmentService;

//    @Test
//    public void testCreateAppointment() {
//        Appointment appointment = Appointment.builder()
//                .appointmentTime(LocalDateTime.of(2026, 10, 27, 17, 6))
//                .reason("Cancer")
//                .build();
//
//        var newAppointment = appointmentService.createNewAppointment(appointment, 1, 2);
//
//        System.out.println(newAppointment);
//    }

    // Re-Assign Appointment to another Doctor
    @Test
    public void testCreateAppointment() {
        Appointment appointment = Appointment.builder()
                .appointmentTime(LocalDateTime.of(2026, 10, 27, 17, 6))
                .reason("Cancer")
                .build();

        var newAppointment = appointmentService.createNewAppointment(appointment, 1, 2);
        System.out.println(newAppointment);

        var upadatedAppointment = appointmentService.reAssignAppointmentToAnotherDoctor(newAppointment.getId(), 3);
        System.out.println(upadatedAppointment);
    }
}
