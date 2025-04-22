# Backend Source Code Documentation

## `/src/main/java/`

### `controllers/`
Contains REST controllers handling HTTP requests for:
- Search functionality for songs, albums, artists
- Playlist management (create, update, delete)
- User authentication endpoints

### `services/`
Business logic implementation including:
- Spotify API integration service
- Playlist management service
- User service
- Search service with filtering capabilities

### `repositories/`
JPA repositories for database operations:
- Song repository
- Album repository
- Artist repository
- Playlist repository
- User repository

### `models/`
Domain entities and DTOs:
- Music-related entities (Song, Album, Artist)
- Playlist entity
- User entity
- Data transfer objects for API responses

### `enums/`
Enumerations for various constants used in the application:
- Language
- User Type

### `security/`
Security-related classes:  
- JWT token generation and validation
- Authentication and authorization filters
- User details service for Spring Security
- Password encoder
- Security configuration

### `config/`
Configuration classes:
- Security config (JWT, authentication)
- Spotify API config
- CORS settings
- WebMVC config

## `/src/main/resources/`

### Configuration Files
- **`application.properties`**:
    - Database connection settings
    - Spotify API credentials
    - Server configuration
    - Security settings