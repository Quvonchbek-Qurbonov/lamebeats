package org.example.lamebeats.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final Jwt Jwt;

    public JwtAuthenticationFilter(Jwt Jwt) {
        this.Jwt = Jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String path = request.getServletPath();

            // Skip filter for public endpoints
            if (path.equals("/api/users/login") || path.equals("/api/users/register")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Check for Authorization header
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract and validate token
            final String token = authHeader.substring(7);
            if (Jwt.validateToken(token)) {
                // Extract user info from token
                String username = Jwt.extractUsername(token);
                UUID userId = Jwt.extractUserId(token);
                String userType = Jwt.extractUserType(token);

                // Create authorities list with user type as role
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userType));

                // Create user details object with extracted claims
                JwtUserDetails userDetails = new JwtUserDetails(userId, username, "", authorities, userType);

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("Failed to set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}