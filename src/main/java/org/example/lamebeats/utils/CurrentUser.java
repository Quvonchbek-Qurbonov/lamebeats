package org.example.lamebeats.utils;

import org.example.lamebeats.enums.UserType;
import org.example.lamebeats.security.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {

    /**
     * Get the currently authenticated user's ID
     */
    public static UUID getCurrentUserId() {
        JwtUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getId() : null;
    }

    /**
     * Get the currently authenticated username
     */
    public static String getCurrentUsername() {
        JwtUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUsername() : null;
    }

    /**
     * Get the currently authenticated user's type
     */
    public static String getCurrentUserType() {
        JwtUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUserType() : null;
    }

    /**
     * Check if the current user has a Admin role
     */
    public static boolean isAdmin() {
        JwtUserDetails userDetails = getCurrentUserDetails();
        return UserType.ADMIN.equals(userDetails.getUserType());
    }

    /**
     * Get the complete JwtUserDetails object of the current user
     */
    private static JwtUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            return (JwtUserDetails) authentication.getPrincipal();
        }
        return null;
    }
}