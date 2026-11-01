package com.example.hospitalManagement.controller;

import com.example.hospitalManagement.dto.LoginRequestDto;
import com.example.hospitalManagement.dto.LoginResponseDto;
import com.example.hospitalManagement.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    // Used for In Memory User Details
//    @PostMapping("/login")
//    public String login(
//            @RequestParam String username,
//            @RequestParam String password) {
//
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//
//        return "Login successful: " + authentication.getName();
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto){

        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @RequestBody LoginRequestDto signupRequestDto){

        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }
}