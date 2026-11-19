package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.type.PermissionType;
import com.example.hospitalManagement.entities.type.RoleType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.hospitalManagement.entities.type.PermissionType.*;
import static com.example.hospitalManagement.entities.type.RoleType.*;

public class RolePermissionMapping {

    private static final Map<RoleType, Set<PermissionType>> map = Map.of(
            PATIENT, Set.of(PATIENT_READ, APPOINTMENT_READ, APPOINTMENT_WRITE),
            DOCTOR, Set.of(APPOINTMENT_DELETE, APPOINTMENT_WRITE, APPOINTMENT_READ, PATIENT_READ),
            ADMIN, Set.of(PATIENT_READ, PATIENT_WRITE, APPOINTMENT_READ, APPOINTMENT_WRITE, APPOINTMENT_DELETE, USER_MANAGE, REPORT_VIEW)
    );

    public static Set<SimpleGrantedAuthority> getGrantedForRoles(RoleType roles){
        return map.getOrDefault(roles, Set.of())
                .stream()
                .map(permissionType -> new SimpleGrantedAuthority(permissionType.getPermission()))
                .collect(Collectors.toSet());
    }
}
