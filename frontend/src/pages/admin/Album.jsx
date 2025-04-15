import React, { useState } from "react";
import { Link } from "react-router-dom";

import {
    Play,
    Pause,
    Heart,
    Share,
    Home,
    Search,
    Library,
    Plus, ChevronLeft, ChevronRight, ChevronDown,
} from "lucide-react";
import logo from "../../assets/logo.png";
import {
    FaExpand,
    FaHeart,
    FaRegHeart,
    FaPause,
    FaPlay,
    FaRandom,
    FaStepBackward,
    FaStepForward,
} from "react-icons/fa";
import { BiChat } from "react-icons/bi";
import { MdQueueMusic } from "react-icons/md";
import { HiSpeakerWave } from "react-icons/hi2";

import Sidebar from "./Sidebar";
const songs = [
    "Snooze",
    "Kill Bill",
    "Seek & Destroy",
    "Blind",
    "Good Days",
    "Used",
    "Notice Me",
    "Gone Girl",
    "Smoking my Ex Pack",
    "Nobody Gets Me",
];

export default function Albumpage() {
    const [isPlaying, setIsPlaying] = useState(false);
    const [isLiked, setIsLiked] = useState(false);

    return (
        <div className="flex h-screen text-white bg-black">
            {/* Sidebar */}
            <Sidebar/>

            {/* Main Content */}
            <main className="flex-1 bg-gradient-to-b from-blue-900 to-black p-8 overflow-y-auto">
                <div className="  w-[1350px] flex ">
                   <span className="fixed top-5 "> <button className=" mr-3 hover:text-rose-500 cursor-pointer">
                        <ChevronLeft/>
                    </button   >
                    <button className="hover:text-rose-500 cursor-pointer"> <ChevronRight/></button></span>

                    <button className="flex fixed right-5 top-5 hover:text-rose-500 cursor-pointer">
                    <img src="" alt="artist" className="rounded-full h-[36px]" />
                        Name
                        <ChevronDown/>
                    </button>
                </div>

              <div className="sticky top-5">
                  <div className="flex items-end space-x-6 ">
                      <img
                          src={logo}
                          alt="Album Cover"
                          className="w-48 h-48 object-cover rounded"
                      />
                      <div>
                          <p className="text-sm uppercase">Album</p>
                          <h1 className="text-6xl font-bold">SOS</h1>
                          <p className="text-sm mt-2">
                              SZA • 2022 • 12 songs • 59 mins 54 seconds
                          </p>
                      </div>
                  </div>

                  <div className="mt-6 flex items-center space-x-4">
                      <button
                          onClick={() => setIsPlaying(!isPlaying)}
                          className="bg-pink-600 p-4 rounded-full hover:bg-pink-700 transition-colors"
                      >
                          {isPlaying ? <Pause size={24} /> : <Play size={24} />}
                      </button>
                      <button onClick={() => setIsLiked(!isLiked)}>
                          {isLiked ? (
                              <FaHeart className="text-pink-500" size={22} />
                          ) : (
                              <FaRegHeart className="hover:text-pink-500" size={22} />
                          )}
                      </button>
                      <Share className="hover:text-pink-500 transition-colors" />
                  </div>
              </div>

                <div className="mt-8">
                    <div className="grid grid-cols-4 text-zinc-400 text-sm border-b border-zinc-700 pb-2">
                        <span>Title</span>
                        <span>Album</span>
                        <span>Plays</span>
                        <span>Time</span>
                    </div>
                    {songs.map((title, i) => (
                        <div
                            key={i}
                            className="grid grid-cols-4 py-2 border-b border-zinc-800"
                        >
                            <span className="text-white">{title}</span>
                            <span className="text-zinc-400">SOS</span>
                            <span className="text-zinc-400">1500</span>
                            <span className="text-zinc-400">04:01</span>
                        </div>
                    ))}
                </div>
            </main>

            {/* Now Playing Bar */}
            <footer className="absolute bottom-0 w-full bg-zinc-900 p-4 flex items-center justify-between">
                <div className="w-full bg-[#121212] text-white px-6 py-3 flex items-center justify-between border-t border-purple-500 rounded-t-2xl">
                    {/* Left Section */}
                    <div className="flex items-center gap-4 w-1/4">
                        <img
                            src="https://i.scdn.co/image/ab67616d00001e02394b2d5357489abed0c83a17"
                            alt="Album Cover"
                            className="w-14 h-14 rounded"
                        />
                        <div>
                            <h4 className="text-sm font-medium">Snooze</h4>
                            <p className="text-xs text-gray-400">SZA</p>
                        </div>
                        <button onClick={() => setIsLiked(!isLiked)}>
                            {isLiked ? (
                                <FaHeart className="ml-2 text-pink-500" />
                            ) : (
                                <FaRegHeart className="ml-2 text-gray-400 hover:text-white" />
                            )}
                        </button>
                    </div>

                    {/* Center Section */}
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
                            <span className="text-xs text-gray-400">3:00</span>
                            <div className="w-full bg-gray-600 h-1 rounded-full overflow-hidden">
                                <div className="w-[80%] h-full bg-white"></div>
                            </div>
                            <span className="text-xs text-gray-400">3:22</span>
                        </div>
                    </div>

                    {/* Right Section */}
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
