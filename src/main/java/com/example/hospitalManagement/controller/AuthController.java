package com.example.hospitalManagement.controller;

import com.example.hospitalManagement.dto.LoginRequestDto;
import com.example.hospitalManagement.dto.LoginResponseDto;
import com.example.hospitalManagement.dto.RefreshTokenRequestDto;
import com.example.hospitalManagement.dto.SignUpRequestDto;
import com.example.hospitalManagement.entities.RefreshToken;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.services.AuthService;
import com.example.hospitalManagement.services.AuthUtil;
import com.example.hospitalManagement.services.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
    private final AuthUtil authUtil;

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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignUpRequestDto signupRequestDto) {

        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {

        RefreshTokenService.RefreshTokenResult result = refreshTokenService.rotateRefreshToken(request.getRefreshToken());

        RefreshToken newToken = result.token();
        User user = newToken.getUser();

        String newAccessToken = authUtil.generateAccessToken(user);

        return ResponseEntity.ok(new LoginResponseDto(newAccessToken, result.rawToken(), user.getId()));
    }
}