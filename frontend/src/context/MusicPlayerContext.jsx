import React, { createContext, useState, useContext, useRef, useEffect } from 'react';

const MusicPlayerContext = createContext();

export const useMusicPlayer = () => useContext(MusicPlayerContext);

export const MusicPlayerProvider = ({ children }) => {
    const [currentSong, setCurrentSong] = useState(null);
    const [queue, setQueue] = useState([]);
    const [isPlaying, setIsPlaying] = useState(false);
    const [isFullScreenMode, setIsFullScreenMode] = useState(false);

    const audioRef = useRef(new Audio());

    // Handle audio source updates
    useEffect(() => {
        if (currentSong?.audioUrl) {
            audioRef.current.src = currentSong.audioUrl;
            if (isPlaying) {
                audioRef.current.play().catch(err => {
                    console.error("Playback error:", err);
                });
            }
        }
    }, [currentSong]);

    // Play/pause logic when isPlaying changes
    useEffect(() => {
        if (!audioRef.current) return;

        if (isPlaying) {
            audioRef.current.play().catch(err => {
                console.error("Playback error:", err);
            });
        } else {
            audioRef.current.pause();
        }
    }, [isPlaying]);

    // Automatically play next when song ends
    useEffect(() => {
        const handleEnded = () => playNext();
        audioRef.current.addEventListener("ended", handleEnded);
        return () => audioRef.current.removeEventListener("ended", handleEnded);
    }, [queue]);

    const playSong = (song, songList = []) => {
        setCurrentSong(song);
        if (songList.length > 0) {
            const remaining = songList.filter(s => s.id !== song.id);
            setQueue(remaining);
        }
        setIsPlaying(true);
    };

    const playNext = () => {
        if (queue.length === 0) return;
        const next = queue[0];
        setQueue(queue.slice(1));
        setCurrentSong(next);
        setIsPlaying(true);
    };

    const playPrevious = () => {
        // Just restart for now
        if (currentSong) {
            audioRef.current.currentTime = 0;
            audioRef.current.play();
        }
    };

    const togglePlay = () => {
        if (currentSong) setIsPlaying(prev => !prev);
    };

    const stopPlayback = () => {
        setIsPlaying(false);
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
    };

    const clearCurrentSong = () => {
        stopPlayback();
        setCurrentSong(null);
    };

    const value = {
        currentSong,
        isPlaying,
        queue,
        isFullScreenMode,
        playSong,
        playNext,
        playPrevious,
        togglePlay,
        setIsPlaying,
        toggleFullScreen: () => setIsFullScreenMode(prev => !prev),
        stopPlayback,
        clearCurrentSong,
    };

    return (
        <MusicPlayerContext.Provider value={value}>
            {children}
            {/* This audio element is hidden but functional */}
            <audio ref={audioRef} />
        </MusicPlayerContext.Provider>
    );
};
