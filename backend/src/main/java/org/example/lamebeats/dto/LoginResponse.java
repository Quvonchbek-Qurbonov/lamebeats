package org.example.lamebeats.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoginResponse {
    private String token;
    private String userAgent;
    private String userType;
    private UUID userId;
    private long tokenTtl;
    private LocalDateTime validUntil;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, String userAgent, String userType, UUID userId, long tokenTtl, LocalDateTime validUntil) {
        this.token = token;
        this.userAgent = userAgent;
        this.userType = userType;
        this.userId = userId;
        this.tokenTtl = tokenTtl;
        this.validUntil = validUntil;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getTokenTtl() {
        return tokenTtl;
    }

    public void setTokenTtl(long tokenTtl) {
        this.tokenTtl = tokenTtl;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}