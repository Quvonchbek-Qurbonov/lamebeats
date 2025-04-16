package org.example.lamebeats.services;

import org.example.lamebeats.models.Song;
import org.example.lamebeats.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@Service
public class SongStreamingService {

    private final SongRepository songRepository;

    @Autowired
    public SongStreamingService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public ResponseEntity<Resource> streamSong(UUID songId, Optional<String> rangeHeader) {
        // Get the song from the repository
        Optional<Song> songOptional = songRepository.findActiveById(songId);
        if (songOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found");
        }

        Song song = songOptional.get();
        String fileUrl = song.getFileUrl();

        try {
            // Create a URL resource from the file URL
            URL url = new URL(fileUrl);
            UrlResource urlResource = new UrlResource(url);

            // Create headers for streaming
            HttpHeaders headers = new HttpHeaders();

            // Try to determine content type
            String contentType = determineContentType(fileUrl);
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            // Enable partial content streaming
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

            // If range header is provided, process it for partial content
            if (rangeHeader.isPresent() && rangeHeader.get().startsWith("bytes=")) {
                return processPartialContent(urlResource, rangeHeader.get(), headers);
            }

            // Return the full resource for normal requests
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(urlResource);

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invalid URL for song file: " + e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error streaming song: " + e.getMessage());
        }
    }

    private ResponseEntity<Resource> processPartialContent(
            UrlResource urlResource, String rangeHeader, HttpHeaders headers) throws IOException {

        // Parse range header (e.g., "bytes=0-1024")
        String range = rangeHeader.substring(6);
        String[] rangeParts = range.split("-");

        long fileSize = urlResource.contentLength();
        long start = 0;
        long end = fileSize - 1;

        if (rangeParts.length > 0 && !rangeParts[0].isEmpty()) {
            start = Long.parseLong(rangeParts[0]);
        }

        if (rangeParts.length > 1 && !rangeParts[1].isEmpty()) {
            end = Long.parseLong(rangeParts[1]);
        }

        if (start > end || start < 0 || end >= fileSize) {
            throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                    "Invalid range request");
        }

        long contentLength = end - start + 1;

        // Create a partial content header
        headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

        // Create a custom resource that reads only the specified portion of the stream
        InputStream is = urlResource.getInputStream();

        // Skip to the start position
        is.skip(start);

        // Create a range-limited input stream resource
        InputStreamResource partialResource = new InputStreamResource(new RangeInputStream(is, contentLength));

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(partialResource);
    }

    /**
     * Custom InputStream that reads only a specified number of bytes
     */
    private static class RangeInputStream extends InputStream {
        private final InputStream delegate;
        private long bytesLeft;

        public RangeInputStream(InputStream delegate, long limit) {
            this.delegate = delegate;
            this.bytesLeft = limit;
        }

        @Override
        public int read() throws IOException {
            if (bytesLeft <= 0) {
                return -1;
            }
            int result = delegate.read();
            if (result >= 0) {
                bytesLeft--;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (bytesLeft <= 0) {
                return -1;
            }

            // Limit the read length to the number of bytes left
            int maxLen = (int) Math.min(len, bytesLeft);
            int result = delegate.read(b, off, maxLen);

            if (result > 0) {
                bytesLeft -= result;
            }

            return result;
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }

    private String determineContentType(String fileUrl) {
        String lowercaseUrl = fileUrl.toLowerCase();

        if (lowercaseUrl.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (lowercaseUrl.endsWith(".wav")) {
            return "audio/wav";
        } else if (lowercaseUrl.endsWith(".ogg")) {
            return "audio/ogg";
        } else if (lowercaseUrl.endsWith(".flac")) {
            return "audio/flac";
        } else if (lowercaseUrl.endsWith(".aac")) {
            return "audio/aac";
        } else if (lowercaseUrl.endsWith(".m4a")) {
            return "audio/mp4";
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}