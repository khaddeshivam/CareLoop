package com.example.hospitalManagement;

import com.example.hospitalManagement.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SecurityTests {
    @Autowired
    private SecurityConfig securityConfig;

    @Test
    public void testPassword() {
        String encoded = securityConfig.passwordEncoder().encode("hello123");
        System.out.println(encoded);
    }
}
