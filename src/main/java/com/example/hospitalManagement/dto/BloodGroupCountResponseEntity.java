package com.example.hospitalManagement.dto;

import com.example.hospitalManagement.entities.type.BloodGroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BloodGroupCountResponseEntity {

    public BloodGroupType bloodGroupType;
    public Long count;
}
