# Frontend Documentation

## Overview
The frontend is a React.js application that provides a user interface for the music search and management system. It integrates with the backend API and offers features for searching songs, albums, and artists through backend.

### `components/`
Reusable UI components:
- **` PrivateRoute.jsx`**: Component for protected routes requiring authentication
- **` Sidebar.jsx`**: Sidebar navigation component
- ** ` player/`**: Music player components
    - `AudioPlayer.jsx`: Main audio player component
    - `PlayerBar.jsx`: Player control bar

### ` context/`
Context providers for global state management:
- **` MusicPlalyerContext.jsx`**: Context for managing audio player state

### ` pages/`
Pages of the application:
- **` admin/`**: Admin dashboard
- **` auth/`**: Authentication pages (login, register)
- **` user/`**: User dashboard