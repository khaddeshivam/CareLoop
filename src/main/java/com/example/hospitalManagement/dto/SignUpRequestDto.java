package com.example.hospitalManagement.dto;

import com.example.hospitalManagement.entities.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private Set<RoleType> roles = new HashSet<>();
}
