package com.example.hospitalManagement.entities.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    MEDICAL_RECORD_READ("doctor:read"),
    MEDICAL_RECORD_UPDATE("doctor:write"),
    PATIENT_READ("patient:read"),
    PATIENT_WRITE("patient:write"),
    APPOINTMENT_READ("appointment:read"),
    APPOINTMENT_WRITE("appointment:write"),
    APPOINTMENT_DELETE("appointment:delete"),
    USER_MANAGE("user:manage"), // For admin tasks
    REPORT_VIEW("report:view");

    private final String permission;
}
