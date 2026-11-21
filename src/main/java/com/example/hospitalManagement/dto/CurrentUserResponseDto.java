package com.example.hospitalManagement.dto;

import com.example.hospitalManagement.entities.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserResponseDto {
    private Long id;
    private String username;
    private Set<RoleType> roles;
}
