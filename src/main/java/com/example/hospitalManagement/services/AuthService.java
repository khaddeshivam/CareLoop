package com.example.hospitalManagement.services;

import com.example.hospitalManagement.controller.SignupResponseDto;
import com.example.hospitalManagement.dto.LoginRequestDto;
import com.example.hospitalManagement.dto.LoginResponseDto;
import com.example.hospitalManagement.dto.SignUpRequestDto;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.entities.type.AuthProviderType;
import com.example.hospitalManagement.entities.type.RoleType;
import com.example.hospitalManagement.repository.PatientRepository;
import com.example.hospitalManagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //    private final AuthProviderType authProviderType;
    private final PatientRepository patientRepository;
    private final RefreshTokenService refreshTokenService;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                ));
        // CustomUserDetailsService returns Spring Security's User, not your JPA entity.

        // Fetch the actual JPA entity after successful authentication
        // User user = (User) authentication.getPrincipal();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT using AuthUtil
        // Now that authentication is successful, your application can issue a JWT.
        String accessToken = authUtil.generateAccessToken(user);

        // Generate and persist refresh accessToken
        String refreshToken = refreshTokenService.createRefreshToken(user).rawToken();

        return new LoginResponseDto(accessToken, refreshToken, user.getId());
    }

    @Transactional
    public User signupInternal(SignUpRequestDto signupRequestDto, String providerId) {
        // Check if user already exists or not
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if (user != null) throw new IllegalArgumentException("User already exists");

        user = userRepository.save(User
                .builder()
                .username(signupRequestDto.getUsername())
                .password(signupRequestDto.getPassword() != null
                        ? passwordEncoder.encode(signupRequestDto.getPassword())
                        : null
                ) // encode the password
                .roles(Set.of(RoleType.PATIENT))
                .build());

//        user = userRepository.save(user);

        // Save in futur if any Patient details changes
        Patient patient = Patient.builder()
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .user(user)
                .build();

        patientRepository.save(patient);

        return user;
    }

    // Login Controller (Basic when using name, email)
    public SignupResponseDto signup(SignUpRequestDto signupRequestDto) {
        User user = signupInternal(signupRequestDto, null);
        return new SignupResponseDto(user.getId(), user.getUsername());
    }

    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        // 1. Fetch providersType and providerId
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        // Check in DB that user exists or not
        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);

        // Check if logged in with email id  or provider username
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User emailUser = userRepository.findByUsername(email).orElse(null);

        // 2. Save the providerType and providerId info with user
        if (user == null && emailUser == null) {
            // signup flow
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = signupInternal(new SignUpRequestDto(username, null, name, null, Set.of(RoleType.PATIENT)), providerId);

            user.setProviderId(providerId);
            user.setProviderType(providerType);

            userRepository.save(user);

        } else if (user != null) {

            // 3. If the user has already account then -> direct login
            if (email != null && !email.isBlank() && !email.equals(user.getUsername())) {
                user.setUsername(email);
                userRepository.save(user);
            }
        } else {

            // 4. orElse -> signup
            throw new BadCredentialsException("This email is already registered with provider " + emailUser.getProviderType());
        }

        String accessToken = authUtil.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).rawToken();

        LoginResponseDto loginResponseDto = new LoginResponseDto(
                accessToken,
                refreshToken,
                user.getId()
        );

        return ResponseEntity.ok(loginResponseDto);
    }
}
