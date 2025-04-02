package org.example.lamebeats.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class SpotifyService {
    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    @Value("${spotify.loginUrl}")
    private String loginUrl;

    private final RestTemplate restTemplate;
    private String accessToken;
    private long tokenExpirationTime;

    public SpotifyService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Initialize the service by fetching the initial token
     */
    @PostConstruct
    public void init() {
        refreshAccessToken();
    }

    /**
     * Schedule token refresh every 58 minutes to prevent expiration
     */
    @Scheduled(fixedRate = 58 * 60 * 1000) // 58 minutes in milliseconds
    public void refreshAccessToken() {
        try {
            SpotifyTokenResponse tokenResponse = fetchTokenFromSpotify();
            if (tokenResponse != null) {
                this.accessToken = tokenResponse.getAccessToken();
                // Set expiration time (current time + expires_in in milliseconds)
                this.tokenExpirationTime = System.currentTimeMillis() + (tokenResponse.getExpiresIn() * 1000);
                System.out.println("Spotify token refreshed successfully");
                System.out.println("Access token: " + this.accessToken);
            } else {
                System.err.println("Received null token response from Spotify");
            }
        } catch (Exception e) {
            System.err.println("Failed to refresh Spotify token: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the current valid access token, refreshing if necessary
     * @return the access token
     */
    public String getAccessToken() {
        // If token is expired or about to expire (within 5 minutes), refresh it
        if (accessToken == null || System.currentTimeMillis() > tokenExpirationTime - (5 * 60 * 1000)) {
            refreshAccessToken();
        }
        return accessToken;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    /**
     * Fetch a new token from Spotify API
     * @return SpotifyTokenResponse object or null if the request fails
     */
    private SpotifyTokenResponse fetchTokenFromSpotify() {
        try {
            // Create request headers with Basic auth
            HttpHeaders headers = new HttpHeaders();
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Use an empty body as required
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(null, headers);

            // Construct the URL with query parameter as required by updated Spotify API
            String tokenUrl = loginUrl + "?grant_type=client_credentials";

            System.out.println("Making request to: " + tokenUrl);

            // First get the raw response as a string to log it
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                    tokenUrl,
                    requestEntity,
                    String.class
            );

            System.out.println("Spotify API response status: " + rawResponse.getStatusCode());
            System.out.println("Spotify API raw response: " + rawResponse.getBody());

            // Now parse the response into our object
            if (rawResponse.getBody() != null) {
                // Create a new RestTemplate for the actual object mapping
                RestTemplate objectMapper = new RestTemplate();
                try {
                    SpotifyTokenResponse tokenResponse = objectMapper.getForObject(
                            "data:" + MediaType.APPLICATION_JSON_VALUE + "," + rawResponse.getBody(),
                            SpotifyTokenResponse.class
                    );

                    // Manually debug the response
                    if (tokenResponse != null) {
                        System.out.println("Parsed token response: access_token=" + tokenResponse.getAccessToken() +
                                ", token_type=" + tokenResponse.getTokenType() +
                                ", expires_in=" + tokenResponse.getExpiresIn());
                    } else {
                        System.out.println("Failed to parse token response");
                    }

                    return tokenResponse;
                } catch (Exception e) {
                    System.err.println("Error parsing token response: " + e.getMessage());
                    e.printStackTrace();

                    // As a fallback, try to directly parse the response
                    try {
                        String responseBody = rawResponse.getBody();

                        // Very basic parsing to extract access_token
                        if (responseBody.contains("access_token")) {
                            int startIndex = responseBody.indexOf("access_token") + 15;
                            int endIndex = responseBody.indexOf("\"", startIndex);
                            String extractedToken = responseBody.substring(startIndex, endIndex);

                            SpotifyTokenResponse manualResponse = new SpotifyTokenResponse();
                            manualResponse.setAccessToken(extractedToken);
                            manualResponse.setTokenType("Bearer");
                            manualResponse.setExpiresIn(3600);

                            System.out.println("Manually extracted token: " + extractedToken);
                            return manualResponse;
                        }
                    } catch (Exception ex) {
                        System.err.println("Manual parsing failed: " + ex.getMessage());
                    }
                }
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error fetching Spotify token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Class to deserialize the Spotify token response
     */
    public static class SpotifyTokenResponse {
        private String access_token;
        private String token_type;
        private int expires_in;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }

        public String getTokenType() {
            return token_type;
        }

        public void setTokenType(String token_type) {
            this.token_type = token_type;
        }

        public int getExpiresIn() {
            return expires_in;
        }

        public void setExpiresIn(int expires_in) {
            this.expires_in = expires_in;
        }
    }
}