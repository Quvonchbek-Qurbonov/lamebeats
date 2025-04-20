import React, { useState, useEffect } from 'react';
import { Play, Pause, SkipBack, SkipForward, Volume2, VolumeX } from 'lucide-react';

const AudioPlayer = ({
                         audioRef,
                         isPlaying,
                         setIsPlaying,
                         onNext,
                         onPrevious,
                         isLoading,
                         error,
                         onPlayToggle
                     }) => {
    const [currentTime, setCurrentTime] = useState(0);
    const [duration, setDuration] = useState(0);
    const [volume, setVolume] = useState(0.7);
    const [isMuted, setIsMuted] = useState(false);

    // Set up listeners for time updates and metadata
    useEffect(() => {
        if (!audioRef?.current) return;

        const audio = audioRef.current;

        const handleTimeUpdate = () => {
            setCurrentTime(audio.currentTime);
        };

        const handleLoadedMetadata = () => {
            setDuration(audio.duration);
        };

        audio.addEventListener('timeupdate', handleTimeUpdate);
        audio.addEventListener('loadedmetadata', handleLoadedMetadata);

        // Set initial values if audio is already loaded
        if (audio.duration) setDuration(audio.duration);
        if (audio.currentTime) setCurrentTime(audio.currentTime);

        return () => {
            audio.removeEventListener('timeupdate', handleTimeUpdate);
            audio.removeEventListener('loadedmetadata', handleLoadedMetadata);
        };
    }, [audioRef]);

    // Handle volume changes
    useEffect(() => {
        if (audioRef?.current) {
            audioRef.current.volume = isMuted ? 0 : volume;
        }
    }, [volume, isMuted, audioRef]);

    // Seek functionality
    const handleSeek = (e) => {
        if (!audioRef?.current) return;

        const seekTime = (e.target.value / 100) * duration;
        setCurrentTime(seekTime);
        audioRef.current.currentTime = seekTime;
    };

    const toggleMute = () => {
        setIsMuted(!isMuted);
    };

    const formatTime = (time) => {
        if (isNaN(time)) return "0:00";

        const minutes = Math.floor(time / 60);
        const seconds = Math.floor(time % 60);
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    };

    return (
        <div className="w-full">
            <div className="flex flex-col gap-2 w-full">
                <div className="flex justify-between items-center text-xs text-gray-400">
                    <span>{formatTime(currentTime)}</span>
                    <span>{formatTime(duration)}</span>
                </div>

                <input
                    type="range"
                    min="0"
                    max="100"
                    value={duration ? (currentTime / duration) * 100 : 0}
                    onChange={handleSeek}
                    className="w-full h-1 bg-gray-600 rounded-lg appearance-none cursor-pointer"
                    style={{
                        background: `linear-gradient(to right, #ef4444 ${(currentTime / duration) * 100}%, #4b5563 ${(currentTime / duration) * 100}%)`
                    }}
                />

                <div className="flex items-center justify-between mt-2">
                    <div className="flex items-center gap-2">
                        <button
                            onClick={toggleMute}
                            className="text-gray-300 hover:text-white p-1"
                        >
                            {isMuted ? <VolumeX size={18}/> : <Volume2 size={18}/>}
                        </button>
                        <input
                            type="range"
                            min="0"
                            max="1"
                            step="0.01"
                            value={volume}
                            onChange={(e) => setVolume(parseFloat(e.target.value))}
                            className="w-20 h-1 bg-gray-600 rounded-lg appearance-none cursor-pointer"
                            style={{
                                background: `linear-gradient(to right, #ef4444 ${volume * 100}%, #4b5563 ${volume * 100}%)`
                            }}
                        />
                    </div>

                    <div className="flex items-center gap-4">
                        <button
                            onClick={onPrevious}
                            className="text-gray-300 hover:text-white"
                        >
                            <SkipBack size={24}/>
                        </button>
                        <button
                            onClick={onPlayToggle}
                            disabled={isLoading}
                            className={`bg-white rounded-full p-2 text-black hover:scale-105 transition ${isLoading ? 'opacity-70' : ''}`}
                        >
                            {isLoading ? (
                                <div className="w-5 h-5 border-2 border-gray-800 border-t-transparent rounded-full animate-spin"/>
                            ) : (
                                isPlaying ? <Pause size={20}/> : <Play size={20} className="ml-0.5"/>
                            )}
                        </button>
                        <button
                            onClick={onNext}
                            className="text-gray-300 hover:text-white"
                        >
                            <SkipForward size={24}/>
                        </button>
                    </div>

                    <div className="w-24">
                        {/* Empty space to balance layout */}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AudioPlayer;