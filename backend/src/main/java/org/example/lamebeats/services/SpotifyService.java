package org.example.lamebeats.services;

import jakarta.annotation.PostConstruct;
import org.example.lamebeats.utils.SpotifyApiProperties;
import org.example.lamebeats.utils.SpotifyPreviewFinder;
import org.example.lamebeats.utils.parsers.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class SpotifyService {
    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    @Value("${spotify.loginUrl}")
    private String loginUrl;

    private final RestTemplate restTemplate;
    private final SpotifyApiProperties spotifyApiProperties;
    private final SpotifyPreviewFinder previewFinder;

    private String accessToken;
    private long tokenExpirationTime;

    public SpotifyService(SpotifyApiProperties spotifyApiProperties) {
        this.restTemplate = new RestTemplate();
        this.spotifyApiProperties = spotifyApiProperties;
        this.previewFinder = new SpotifyPreviewFinder(clientId, clientSecret);
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
            SpotifyTokenParser.SpotifyTokenResponse tokenResponse = fetchTokenFromSpotify();
            if (tokenResponse != null) {
                this.accessToken = tokenResponse.getAccessToken();
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
    private SpotifyTokenParser.SpotifyTokenResponse fetchTokenFromSpotify() {
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
                    SpotifyTokenParser.SpotifyTokenResponse tokenResponse = objectMapper.getForObject(
                            "data:" + MediaType.APPLICATION_JSON_VALUE + "," + rawResponse.getBody(),
                            SpotifyTokenParser.SpotifyTokenResponse.class
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

                            SpotifyTokenParser.SpotifyTokenResponse manualResponse = new SpotifyTokenParser.SpotifyTokenResponse();
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

    public SpotifySearchParser.SearchResult search(String query, String types, int page, int limit) {
        int offset = (page - 1) * limit; // Convert page to offset

        // Create the search URL with specified types
        String searchUrl = spotifyApiProperties.getBaseUrl() +
                spotifyApiProperties.getResources().getSearch() +
                query +
                "&type=" + types +
                "&offset=" + offset +
                "&limit=" + limit +
                "&market=UZ";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Parse the response using our parser
                return SpotifySearchParser.parseSearchResponse(response.getBody());
            } else {
                System.err.println("Error fetching search results: " + response.getStatusCode());
                return new SpotifySearchParser.SearchResult();
            }
        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            e.printStackTrace();
            return new SpotifySearchParser.SearchResult();
        }
    }

    public SpotifyTrackParser getTrackByIdFromSpotify(String trackId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = spotifyApiProperties.getBaseUrl() + spotifyApiProperties.getResources().getTracks() + "/" + trackId;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return SpotifyTrackParser.parseTrack(response.getBody());
        } else {
            throw new RuntimeException("Failed to retrieve track with ID: " + trackId);
        }
    }

    public SpotifyAlbumParser getAlbumByIdFromSpotify(String albumId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = spotifyApiProperties.getBaseUrl() + spotifyApiProperties.getResources().getAlbums() + "/" + albumId;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return SpotifyAlbumParser.parseAlbum(response.getBody());
        } else {
            throw new RuntimeException("Failed to retrieve album with ID: " + albumId);
        }
    }

    public SpotifyArtistParser getArtistByIdFromSpotify(String artistId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = spotifyApiProperties.getBaseUrl() + spotifyApiProperties.getResources().getArtists() + "/" + artistId;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return SpotifyArtistParser.parseArtist(response.getBody());
        } else {
            throw new RuntimeException("Failed to retrieve artist with ID: " + artistId);
        }
    }

    public SpotifyAlbumTracks getAlbumTracks(String albumId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = spotifyApiProperties.getBaseUrl() + spotifyApiProperties.getResources().getAlbums() + "/" + albumId + "/tracks?market=UZ";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return SpotifyAlbumTracks.parseAlbumTracks(response.getBody());
        } else {
            throw new RuntimeException("Failed to retrieve tracks for album with ID: " + albumId);
        }
    }

    public List<String> getTrackPreviewUrls(String trackId) {
        try {
            return previewFinder.getPreviewUrls(trackId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get preview URLs: " + e.getMessage(), e);
        }
    }
}