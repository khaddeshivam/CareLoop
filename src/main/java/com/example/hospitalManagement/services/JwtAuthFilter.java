package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final AuthenticationEntryPoint authenticationEntryPoint;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            log.info("incoming request: {}", request.getRequestURI());
//

    /// /        final String requestTokenHeader = request.getHeader("Authorization");
    /// /
    /// /        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
    /// /            filterChain.doFilter(request, response);
    /// /            return;
    /// /        }
    /// /
    /// /        String token = requestTokenHeader.split("Bearer")[1];
//
//            String authHeader = request.getHeader("Authorization");
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//            String token = authHeader.substring(7);
//
//            String username = authUtil.getUsernameFromToken(token);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                User user = userRepository.findByUsername(username).orElseThrow();
//
//                // we Fill filters into security context after authenticated
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(
//                                user,
//                                null,
//                                user.getAuthorities()
//                        );
//
//                // Now update Security context to be filled
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            }
//
//            filterChain.doFilter(request, response);
//        } catch (Exception ex) {
//
//            handlerExceptionResolver.resolveException(request, response, null, ex);
//        }
//    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("Incoming request: {}", request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        // No Bearer token: let Spring Security decide access.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Check Empty or malformed Bearer token
        if(token.isBlank()){
            log.warn("Empty JWT token received");

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid or expired JWT")
            );

            return;
        }

        try {
            // Extract username from JWT.
            String username = authUtil.getUsernameFromToken(token);

            if (username == null || username.isBlank()) {
                throw new JwtException("JWT subject is missing");
            }

            // Authenticate only if no authentication exists.
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found")
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (JwtException | UsernameNotFoundException ex) {
            log.warn("JWT authentication failed: {}", ex.getClass().getSimpleName());
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid or expired JWT", ex)
            );

            return;
        }

        // Continue only after successful processing.
        filterChain.doFilter(request, response);
    }
}
