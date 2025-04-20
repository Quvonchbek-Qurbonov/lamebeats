import React, { useRef, useState, useEffect } from 'react';
import { Play, Pause, SkipBack, SkipForward, Volume2, VolumeX } from 'lucide-react';

const AudioPlayer = ({
                         audioUrl,
                         songInfo,
                         isPlaying,
                         setIsPlaying,
                         onNext,
                         onPrevious,
                         onEnded
                     }) => {
    const audioRef = useRef(null);
    const [currentTime, setCurrentTime] = useState(0);
    const [duration, setDuration] = useState(0);
    const [volume, setVolume] = useState(0.7);
    const [isMuted, setIsMuted] = useState(false);

    useEffect(() => {
        if (isPlaying) {
            audioRef.current.play().catch(error => {
                console.error("Playback error:", error);
                setIsPlaying(false);
            });
        } else {
            audioRef.current.pause();
        }
    }, [isPlaying, audioUrl]);

    useEffect(() => {
        if (audioRef.current) {
            audioRef.current.volume = isMuted ? 0 : volume;
        }
    }, [volume, isMuted]);

    const handleTimeUpdate = () => {
        setCurrentTime(audioRef.current.currentTime);
    };

    const handleLoadedMetadata = () => {
        setDuration(audioRef.current.duration);
    };

    const handleSeek = (e) => {
        const seekTime = (e.target.value / 100) * duration;
        setCurrentTime(seekTime);
        audioRef.current.currentTime = seekTime;
    };

    const togglePlay = () => {
        setIsPlaying(!isPlaying);
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
            <audio
                ref={audioRef}
                src={audioUrl}
                onTimeUpdate={handleTimeUpdate}
                onLoadedMetadata={handleLoadedMetadata}
                onEnded={onEnded}
            />

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
                            {isMuted ? <VolumeX size={18} /> : <Volume2 size={18} />}
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
                            <SkipBack size={24} />
                        </button>
                        <button
                            onClick={togglePlay}
                            className="bg-white rounded-full p-2 text-black hover:scale-105 transition"
                        >
                            {isPlaying ? <Pause size={20} /> : <Play size={20} className="ml-0.5" />}
                        </button>
                        <button
                            onClick={onNext}
                            className="text-gray-300 hover:text-white"
                        >
                            <SkipForward size={24} />
                        </button>
                    </div>

                    <div className="w-24">
                        {/* Placeholder to balance the layout */}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AudioPlayer;