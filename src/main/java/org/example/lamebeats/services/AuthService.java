package org.example.lamebeats.services;

import org.example.lamebeats.dto.LoginRequest;
import org.example.lamebeats.dto.LoginResponse;
import org.example.lamebeats.models.User;
import org.example.lamebeats.security.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;
    private final Jwt Jwt;

    @Autowired
    public AuthService(UserService userService, Jwt Jwt) {
        this.userService = userService;
        this.Jwt = Jwt;
    }

    public LoginResponse login(LoginRequest loginRequest, String userAgent) {
        // Find active user by username
        Optional<User> optionalUser = userService.getActiveUserByUsername(loginRequest.getUsername());

        // If no user found, try email
        if (optionalUser.isEmpty()) {
            optionalUser = userService.getActiveUserByEmail(loginRequest.getUsername());
        }

        // Throw exception if user not found
        User user = optionalUser.orElseThrow(() ->
                new BadCredentialsException("Invalid username/email or password"));

        // Verify password
        if (!userService.verifyPassword(user, loginRequest.getPassword())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        // Generate JWT token
        String token = Jwt.generateToken(user);

        // Get expiration date
        Date expirationDate = Jwt.extractExpiration(token);
        LocalDateTime validUntil = Jwt.convertToLocalDateTime(expirationDate);

        // Create and return login response
        return new LoginResponse(
                token,
                userAgent,
                user.getId(),
                Jwt.getTokenTtl(),
                validUntil
        );
    }
}