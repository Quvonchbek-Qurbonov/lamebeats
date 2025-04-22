# Project Overview
# LameBeats
LameBeats is a music search and management system that integrates with the Spotify API. It allows users to search for songs, albums, and artists, manage playlists, and play music.

## Description
The backend is a Spring Boot application that provides REST APIs for a music search and management system. It integrates with the Spotify API to fetch music data and manages user playlists. The frontend is a React.js application that provides a user-friendly interface for interacting with the backend APIs.

## Key Directories

### `src/main/java/`
- **`controllers/`**: REST endpoints for music search and playlist management
- **`services/`**: Business logic for Spotify integration and playlist operations
- **`repositories/`**: Data access layer with JPA repositories
- **`models/`**: Domain models for songs, albums, artists and playlists
- **`configs/`**: Security and API configurations
- **` enums/`**: Enumerations for various constants used in the application
- **` security/`**: Security configurations and JWT handling
- **` utils/`**: Utility classes for common operations

### `src/main/resources/`
- **`application.properties`**: Configuration for database, Spotify API credentials