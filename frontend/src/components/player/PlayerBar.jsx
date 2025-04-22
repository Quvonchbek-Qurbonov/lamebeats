import React, { useRef, useState, useEffect } from 'react';
import { useMusicPlayer } from '../../context/MusicPlayerContext';
import AudioPlayer from './AudioPlayer';
import { Maximize2, X } from 'lucide-react';

const PlayerBar = () => {
    const {
        currentSong,
        isPlaying,
        setIsPlaying,
        playNext,
        playPrevious,
        toggleFullScreen,
        isFullScreenMode,
        stopPlayback,
        clearCurrentSong
    } = useMusicPlayer();

    // Create a single audio element ref at the PlayerBar level
    const audioRef = useRef(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    // Fetch preview URL function at the PlayerBar level
    const fetchPreviewUrl = async () => {
        if (!currentSong) return;

        setIsLoading(true);
        setError(null);

        try {
            const songId = currentSong.spotifyId || currentSong.id;

            if (!songId) {
                throw new Error("No valid song ID found");
            }

            // Try to use current domain's API endpoint
            const apiUrl = `http://35.209.62.223/api/songs/${songId}/preview`;

            console.log(`Fetching preview from: ${apiUrl}`);

            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('token'),
                }
            });

            if (!response.ok) {
                throw new Error(`API error: ${response.status}`);
            }

            const data = await response.json();

            if (!data.url) {
                throw new Error("No preview URL in response");
            }

            console.log("Setting preview URL:", data.url);
            setPreviewUrl(data.url);
            return data.url;

        } catch (err) {
            console.error("Error fetching preview URL:", err);
            setError("Failed to fetch preview");
            throw err;
        } finally {
            setIsLoading(false);
        }
    };

    // Handle closing the player
    const handleClosePlayer = () => {
        if (audioRef.current) {
            audioRef.current.pause();
        }
        setIsPlaying(false);
        clearCurrentSong(); // This will clear the current song and hide the player
    };

    // Handle song changes
    useEffect(() => {
        if (currentSong) {
            // Reset states for new song
            setPreviewUrl('');
            setError(null);

            // If auto-play is enabled, fetch the preview
            if (isPlaying) {
                fetchPreviewUrl().catch(err => {
                    console.error("Failed to fetch on song change:", err);
                    setIsPlaying(false);
                });
            }
        }
    }, [currentSong]);

    // Handle play/pause changes
    useEffect(() => {
        if (!audioRef.current) return;

        if (isPlaying) {
            // Need to fetch URL if we don't have one
            if (!previewUrl && currentSong) {
                fetchPreviewUrl().then(() => {
                    // URL is now set, play will be handled in the next effect
                }).catch(() => {
                    setIsPlaying(false);
                });
            } else if (previewUrl) {
                // We have a URL, play it
                audioRef.current.play().catch(err => {
                    console.error("Play error:", err);
                    setIsPlaying(false);
                });
            }
        } else {
            // Pause the audio
            audioRef.current.pause();
        }
    }, [isPlaying, previewUrl, currentSong]);

    // Update audio element when URL changes
    useEffect(() => {
        if (audioRef.current && previewUrl) {
            audioRef.current.src = previewUrl;

            if (isPlaying) {
                audioRef.current.play().catch(err => {
                    console.error("Play error after URL change:", err);
                    setIsPlaying(false);
                });
            }
        }
    }, [previewUrl]);

    if (!currentSong) return null;

    return (
        <>
            {/* Single shared audio element for both views */}
            <audio
                ref={audioRef}
                src={previewUrl}
                onEnded={playNext}
                onError={() => setError("Error playing track")}
                // Important: do not add display:none - it can cause issues on some browsers
            />

            {/* Regular player bar at bottom */}
            <div className={`fixed bottom-0 left-0 right-0 bg-gradient-to-r from-red-900/95 to-black/95 border-t border-red-800 p-3 z-40 ${isFullScreenMode ? 'hidden' : 'flex'}`}>
                <div className="container mx-auto flex items-center gap-4">
                    <div className="flex-shrink-0 w-14 h-14 bg-red-800 rounded overflow-hidden">
                        {currentSong.album?.images && (
                            <img
                                src={currentSong.album.images[0]}
                                alt={currentSong.title}
                                className="w-full h-full object-cover"
                            />
                        )}
                    </div>

                    <div className="flex-1 min-w-0">
                        <h4 className="text-white font-medium truncate">{currentSong.title}</h4>
                        <p className="text-gray-400 text-sm truncate">
                            {currentSong.artists?.map(artist => artist.name).join(', ')}
                        </p>
                        {error && (
                            <p className="text-red-500 text-xs">{error}</p>
                        )}
                    </div>

                    <div className="flex-1 max-w-xl">
                        <AudioPlayer
                            audioRef={audioRef}
                            isPlaying={isPlaying}
                            setIsPlaying={setIsPlaying}
                            onNext={playNext}
                            onPrevious={playPrevious}
                            isLoading={isLoading}
                            error={error}
                            onPlayToggle={() => {
                                if (!isPlaying && !previewUrl) {
                                    fetchPreviewUrl().then(() => {
                                        setIsPlaying(true);
                                    }).catch(() => {});
                                } else {
                                    setIsPlaying(!isPlaying);
                                }
                            }}
                        />
                    </div>

                    <div className="flex items-center gap-2">
                        <button
                            onClick={toggleFullScreen}
                            className="text-gray-300 hover:text-white p-2"
                        >
                            <Maximize2 size={20} />
                        </button>

                        {/* Close button */}
                        <button
                            onClick={handleClosePlayer}
                            className="text-gray-300 hover:text-red-400 p-2 rounded-full"
                            title="Close player"
                        >
                            <X size={20} />
                        </button>
                    </div>
                </div>
            </div>

            {/* Full screen overlay */}
            {isFullScreenMode && (
                <div className="fixed inset-0 bg-gradient-to-b from-red-900 to-black z-50 flex flex-col">
                    <div className="absolute top-4 right-4">
                        <button
                            onClick={stopPlayback}
                            className="text-gray-300 hover:text-white p-2 rounded-full bg-black/30 hover:bg-black/50 transition"
                        >
                            <X size={24} />
                        </button>
                    </div>

                    <div className="flex-1 flex flex-col items-center justify-center p-8">
                        <div className="w-64 h-64 md:w-80 md:h-80 bg-red-800 rounded-lg shadow-2xl overflow-hidden mb-8">
                            {currentSong.album?.images && (
                                <img
                                    src={currentSong.album.images[0]}
                                    alt={currentSong.title}
                                    className="w-full h-full object-cover"
                                />
                            )}
                        </div>

                        <h2 className="text-white text-2xl font-bold text-center mt-4">{currentSong.title}</h2>
                        <p className="text-gray-300 text-center mt-2">
                            {currentSong.artists?.map(artist => artist.name).join(', ')}
                        </p>
                        {error && (
                            <p className="text-red-500 text-sm mt-2">{error}</p>
                        )}
                    </div>

                    <div className="p-8 max-w-lg mx-auto w-full">
                        <AudioPlayer
                            audioRef={audioRef}
                            isPlaying={isPlaying}
                            setIsPlaying={setIsPlaying}
                            onNext={playNext}
                            onPrevious={playPrevious}
                            isLoading={isLoading}
                            error={error}
                            onPlayToggle={() => {
                                if (!isPlaying && !previewUrl) {
                                    fetchPreviewUrl().then(() => {
                                        setIsPlaying(true);
                                    }).catch(() => {});
                                } else {
                                    setIsPlaying(!isPlaying);
                                }
                            }}
                        />
                    </div>
                </div>
            )}
        </>
    );
};

export default PlayerBar;