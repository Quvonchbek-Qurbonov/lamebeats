import React, { useState } from "react";
import Sidebar from "./Sidebar";
import {
    ChevronLeft,
    ChevronRight,
    ChevronDown,
    Play,
    Pause,
    Share, Shuffle,
} from "lucide-react";
import {
    FaHeart,
    FaRegHeart,
    FaPause,
    FaPlay,
    FaRandom,
    FaStepBackward,
    FaStepForward,
    FaExpand,
} from "react-icons/fa";
import { BiChat } from "react-icons/bi";
import { MdQueueMusic } from "react-icons/md";
import { HiSpeakerWave } from "react-icons/hi2";
import {FaShuffle} from "react-icons/fa6";

const playlistSongs = [
    "First Song",
    "Second Song",
    "Third Song",
    "Fourth Song",
    "Fifth Song",
];

export default function PlaylistPage() {
    const [isPlaying, setIsPlaying] = useState(false);
    const [isLiked, setIsLiked] = useState(false);

    return (
        <div className="flex h-screen text-white bg-black">
            <Sidebar />

            <main className="flex-1 bg-gradient-to-b from-gray-800 to-black p-8 overflow-y-auto">
                {/* Header */}
                <div className="w-full flex justify-between">
                    <div className="flex items-center space-x-2 fixed top-5">
                        <button className="hover:text-rose-500">
                            <ChevronLeft />
                        </button>
                        <button className="hover:text-rose-500">
                            <ChevronRight />
                        </button>
                    </div>
                    <div className="flex items-center fixed top-5 right-5 hover:text-rose-500 cursor-pointer">
                        <img
                            src=""
                            alt="User"
                            className="rounded-full h-[36px]"
                        />
                        <span>Name</span>
                        <ChevronDown />
                    </div>
                </div>

                {/* Playlist Details */}
                <div className="sticky top-5">
                    <div className="flex items-end space-x-6">
                        <div className="w-48 h-48 bg-zinc-700 rounded"></div>
                        <div>
                            <p className="text-sm uppercase">Playlist</p>
                            <h1 className="text-6xl font-bold">My Playlist</h1>
                            <p className="text-sm mt-2 flex items-center space-x-2">
                                <img src="" alt="artist" className=" h-[28px] rounded-full"/>•name• 5 songs
                            </p>
                        </div>
                    </div>

                    <div className="mt-6 flex items-center space-x-4">
                        <button
                            onClick={() => setIsPlaying(!isPlaying)}
                            className="bg-green-500 p-4 rounded-full hover:bg-green-600 transition-colors"
                        >
                            {isPlaying ? <Pause size={24} /> : <Play size={24} />}
                        </button>

                      <Shuffle/>
                    </div>
                </div>

                {/* Song List */}
                <div className="mt-8">
                    <div className="grid grid-cols-4 text-zinc-400 text-sm border-b border-zinc-700 pb-2">
                        <span>Title</span>
                        <span>Album</span>
                        <span>Plays</span>
                        <span>Time</span>
                    </div>
                    {playlistSongs.map((title, i) => (
                        <div
                            key={i}
                            className="grid grid-cols-4 py-2 border-b border-zinc-800"
                        >
                            <span className="text-white">{title}</span>
                            <span className="text-zinc-400">Vibes</span>
                            <span className="text-zinc-400">10,000</span>
                            <span className="text-zinc-400">03:45</span>
                        </div>
                    ))}
                </div>
            </main>

            {/* Now Playing Bar */}
            <footer className="absolute bottom-0 w-full bg-zinc-900 p-4 flex items-center justify-between">
                <div className="w-full bg-[#121212] text-white px-6 py-3 flex items-center justify-between border-t border-purple-500 rounded-t-2xl">
                    <div className="flex items-center gap-4 w-1/4">
                        <div className="w-14 h-14 bg-zinc-700 rounded"></div>
                        <div>
                            <h4 className="text-sm font-medium">First Song</h4>
                            <p className="text-xs text-gray-400">Artist</p>
                        </div>
                        <button onClick={() => setIsLiked(!isLiked)}>
                            {isLiked ? (
                                <FaHeart className="ml-2 text-green-500" />
                            ) : (
                                <FaRegHeart className="ml-2 text-gray-400 hover:text-white" />
                            )}
                        </button>
                    </div>

                    <div className="flex flex-col items-center w-2/4">
                        <div className="flex gap-6 items-center mb-1">
                            <FaRandom className="text-gray-400 hover:text-white cursor-pointer" />
                            <FaStepBackward className="cursor-pointer" />
                            <button
                                className="bg-white text-black rounded-full p-2"
                                onClick={() => setIsPlaying(!isPlaying)}
                            >
                                {isPlaying ? <FaPause size={14} /> : <FaPlay size={14} />}
                            </button>
                            <FaStepForward className="cursor-pointer" />
                            <BiChat className="text-gray-400 hover:text-white cursor-pointer" />
                        </div>
                        <div className="flex items-center gap-2 w-full">
                            <span className="text-xs text-gray-400">1:10</span>
                            <div className="w-full bg-gray-600 h-1 rounded-full overflow-hidden">
                                <div className="w-[40%] h-full bg-white"></div>
                            </div>
                            <span className="text-xs text-gray-400">3:45</span>
                        </div>
                    </div>

                    <div className="flex items-center gap-4 w-1/4 justify-end">
                        <MdQueueMusic className="text-gray-400 hover:text-white cursor-pointer" />
                        <HiSpeakerWave className="text-gray-400 hover:text-white cursor-pointer" />
                        <div className="w-20 bg-gray-600 h-1 rounded-full overflow-hidden">
                            <div className="w-[50%] h-full bg-white"></div>
                        </div>
                        <FaExpand className="text-gray-400 hover:text-white cursor-pointer" />
                    </div>
                </div>
            </footer>
        </div>
    );
}
