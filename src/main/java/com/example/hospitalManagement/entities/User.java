package com.example.hospitalManagement.entities;

import com.example.hospitalManagement.entities.type.AuthProviderType;
import com.example.hospitalManagement.entities.type.RoleType;
import com.example.hospitalManagement.services.RolePermissionMapping;
import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {@Index(name = "idx_provider_id_provider_type", columnList = "providerId, providerType")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @JoinColumn(unique = true)
    @Column(nullable = false, unique = true)
    private String username;

    //    @Column(nullable = false)
    private String password;

//    @Column(nullable = false)
//    private String role;

    //    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//
//        if (role == null || role.isBlank()) {
//            return List.of();
//        }
//
//        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
//    }
    // Granular Level security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleType role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            authorities.addAll(RolePermissionMapping.getGrantedForRoles(role));
        }

        return authorities;
    }

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProviderType providerType;

    // Adding Multiple Roles
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    public Set<RoleType> roles = new HashSet<>();
}