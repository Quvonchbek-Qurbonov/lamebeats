package org.example.lamebeats.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpotifyPreviewFinder {
    
    private final String clientId;
    private final String clientSecret;
    private final RestTemplate restTemplate;
    
    public SpotifyPreviewFinder(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Get preview URLs for a Spotify track by its ID
     * @param trackId Spotify track ID
     * @return List of preview URLs found
     * @throws IOException If connection fails
     */
    public List<String> getPreviewUrls(String trackId) throws IOException {
        String spotifyUrl = "https://open.spotify.com/track/" + trackId;
        return getSpotifyLinks(spotifyUrl);
    }
    
    private List<String> getSpotifyLinks(String url) throws IOException {
        // Use Jsoup for HTML parsing (equivalent to cheerio in JS)
        Document doc = Jsoup.connect(url)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .get();
        
        Set<String> scdnLinks = new HashSet<>();
        
        // Find all elements
        Elements allElements = doc.select("*");
        
        // Extract all attributes that contain p.scdn.co
        for (Element element : allElements) {
            for (String attrName : element.attributes().asList().stream()
                    .map(attr -> attr.getKey())
                    .collect(Collectors.toList())) {
                
                String attrValue = element.attr(attrName);
                if (attrValue != null && attrValue.contains("p.scdn.co")) {
                    scdnLinks.add(attrValue);
                }
            }
        }
        
        return scdnLinks.stream().collect(Collectors.toList());
    }
}