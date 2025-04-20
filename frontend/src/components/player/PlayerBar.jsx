import React, { useState, useEffect } from 'react';
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
        stopPlayback
    } = useMusicPlayer();

    const [audioUrl, setAudioUrl] = useState('');

    useEffect(() => {
        if (currentSong?.id) {
            setAudioUrl(`http://lamebeats.steamfest.live/api/songs/${currentSong.id}/stream`);
        }
    }, [currentSong]);

    if (!currentSong) return null;

    return (
        <>
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
                    </div>

                    <div className="flex-1 max-w-xl">
                        <AudioPlayer
                            audioUrl={audioUrl}
                            songInfo={currentSong}
                            isPlaying={isPlaying}
                            setIsPlaying={setIsPlaying}
                            onNext={playNext}
                            onPrevious={playPrevious}
                            onEnded={playNext}
                        />
                    </div>

                    <button
                        onClick={toggleFullScreen}
                        className="ml-4 text-gray-300 hover:text-white p-2"
                    >
                        <Maximize2 size={20} />
                    </button>
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
                    </div>

                    <div className="p-8 max-w-lg mx-auto w-full">
                        <AudioPlayer
                            audioUrl={audioUrl}
                            songInfo={currentSong}
                            isPlaying={isPlaying}
                            setIsPlaying={setIsPlaying}
                            onNext={playNext}
                            onPrevious={playPrevious}
                            onEnded={playNext}
                        />
                    </div>
                </div>
            )}
        </>
    );
};

export default PlayerBar;