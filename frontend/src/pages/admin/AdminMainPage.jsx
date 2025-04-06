import React from "react";
import { Search, Home, Library, Plus, Heart } from "lucide-react";
import {useNavigate} from "react-router-dom";

export default function AdminMainPage() {
    const navigate = useNavigate();

    const handleLogout = () => {
        navigate("/login");
    };

    return (
        <div className="h-screen bg-black text-white flex overflow-hidden">
            {/* Sidebar */}
            <aside className="w-60 bg-black p-6 flex flex-col justify-between">
                <div>
                    <h1 className="text-2xl font-bold text-red-500 mb-10">LameBeats</h1>
                    <nav className="space-y-4 text-sm">
                        <a href="#" className="flex items-center gap-3 hover:text-red-400">
                            <Home size={18} /> Home
                        </a>
                        <a href="#search" className="flex items-center gap-3 hover:text-red-400">
                            <Search size={18} /> Search
                        </a>
                        <a href="#library" className="flex items-center gap-3 hover:text-red-400">
                            <Library size={18} /> Your Library
                        </a>
                    </nav>

                    <div className="mt-10 space-y-4 text-sm">
                        <a href="#" className="flex items-center gap-3 hover:text-red-400">
                            <Plus size={18} /> Create Playlist
                        </a>
                        <a href="#" className="flex items-center gap-3 hover:text-red-400">
                            <Heart size={18} /> Liked Songs
                        </a>
                    </div>
                </div>

                <div className="text-xs text-gray-400 space-y-1">
                    <button
                        onClick={handleLogout}
                        className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-full transition duration-300 ease-in-out"
                    >
                        Logout
                    </button>
                </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 bg-gradient-to-b from-red-700/80 via-black to-black p-6 overflow-y-auto h-full">
                <div className="h-full max-h-full">
                    <header
                        className="flex items-center justify-between mb-8 sticky top-0 z-10 backdrop-blur bg-red-700/40 p-4 rounded">
                    <input
                            type="text"
                            placeholder="Search by Artist, Song, or Album"
                            className="bg-red-900/70 text-white px-4 py-2 rounded-full w-full max-w-md placeholder:text-red-300 focus:outline-none border border-red-400/30"
                        />
                        <div className="ml-4 bg-red-800/80 px-4 py-2 rounded-full font-semibold">Cody Fisher</div>
                    </header>

                    {/* Focus Section */}
                    <section className="mb-10">
                        <h2 className="text-2xl font-bold mb-4">Focus</h2>
                        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-5">
                            {Array.from({ length: 5 }).map((_, i) => (
                                <div key={i} className="bg-neutral-900 p-4 rounded-lg hover:bg-neutral-800 transition">
                                    <div className="aspect-square bg-red-700 rounded mb-3"></div>
                                    <h3 className="font-semibold">Focus {i + 1}</h3>
                                    <p className="text-sm opacity-70">Chill and concentrate with lo-fi and ambient sounds.</p>
                                </div>
                            ))}
                        </div>
                    </section>

                    {/* Playlists Section */}
                    <section>
                        <h2 className="text-2xl font-bold mb-4">Playlists</h2>
                        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-5">
                            {Array.from({ length: 5 }).map((_, i) => (
                                <div key={i} className="bg-neutral-900 p-4 rounded-lg hover:bg-neutral-800 transition">
                                    <div className="aspect-square bg-red-700 rounded mb-3"></div>
                                    <h3 className="font-semibold">Playlist {i + 1}</h3>
                                    <p className="text-sm opacity-70">Various tracks for different moods.</p>
                                </div>
                            ))}
                        </div>
                    </section>
                </div>
            </main>
        </div>
    );
}