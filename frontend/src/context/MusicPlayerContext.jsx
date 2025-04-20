import React, { createContext, useState, useContext, useEffect } from 'react';

const MusicPlayerContext = createContext();

export const useMusicPlayer = () => useContext(MusicPlayerContext);

export const MusicPlayerProvider = ({ children }) => {
    const [currentSong, setCurrentSong] = useState(null);
    const [queue, setQueue] = useState([]);
    const [isPlaying, setIsPlaying] = useState(false);
    const [isFullScreenMode, setIsFullScreenMode] = useState(false);

    const playSong = (song, songList = []) => {
        setCurrentSong(song);

        // If songList is provided, set it as the queue, excluding the current song
        if (songList.length > 0) {
            const remainingSongs = songList.filter(s => s.id !== song.id);
            setQueue(remainingSongs);
        }

        setIsPlaying(true);
    };

    const playNext = () => {
        if (queue.length === 0) return;

        const nextSong = queue[0];
        const newQueue = queue.slice(1);

        setCurrentSong(nextSong);
        setQueue(newQueue);
        setIsPlaying(true);
    };

    const playPrevious = () => {
        // This is simplified - a real implementation would need to maintain
        // a history of played songs
        if (!currentSong) return;
        // For now, just restart the current song
        if (currentSong) {
            setCurrentSong({...currentSong});
        }
    };

    const togglePlay = () => {
        if (currentSong) {
            setIsPlaying(!isPlaying);
        }
    };

    const toggleFullScreen = () => {
        setIsFullScreenMode(!isFullScreenMode);
    };

    const stopPlayback = () => {
        setIsPlaying(false);
        setIsFullScreenMode(false);
    };

    const value = {
        currentSong,
        isPlaying,
        isFullScreenMode,
        queue,
        playSong,
        playNext,
        playPrevious,
        togglePlay,
        setIsPlaying,
        toggleFullScreen,
        stopPlayback
    };

    return (
        <MusicPlayerContext.Provider value={value}>
            {children}
        </MusicPlayerContext.Provider>
    );
};