import React from 'react';
import { Home, Search } from 'lucide-react'; // Importing icons

export default function ArtistProfilePage() {
    return (
        <div className="flex h-screen text-white bg-black">
            {/* Sidebar */}
            <aside className="w-64 bg-zinc-900 p-4 overflow-y-auto">
                <div className="mb-6">
                    <h1 className="text-2xl font-bold">Lame Beats</h1>
                </div>

                {/* Search Box */}
                <div className="mb-6">
                    <input
                        type="text"
                        placeholder="Search for artist or song..."
                        className="w-full p-2 rounded-md bg-zinc-800 text-white text-sm placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-red-600"
                    />
                </div>

                <nav className="space-y-4">
                    <div className="flex items-center gap-2 cursor-pointer hover:text-white text-zinc-400">
                        <Home size={18} />
                        <span>Home</span>
                    </div>
                    <div className="flex items-center gap-2 cursor-pointer hover:text-white text-zinc-400">
                        <Search size={18} />
                        <span>Search</span>
                    </div>
                    <div>Your Library</div>
                    <div>Create Playlist</div>
                    <div>Liked Songs</div>
                    <div className="mt-4 border-t border-zinc-700 pt-4 text-sm space-y-1">
                        {Array.from({ length: 30 }).map((_, i) => (
                            <div key={i} className="text-zinc-400 hover:text-white cursor-pointer">
                                Genre {i + 1}
                            </div>
                        ))}
                    </div>
                </nav>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-y-auto">
                {/* Artist Header */}
                <div className="bg-gradient-to-b from-red-700 to-black p-6 flex items-end gap-6">
                    <img
                        src="https://randomuser.me/api/portraits/men/32.jpg"
                        alt="Artist"
                        className="w-32 h-32 rounded-full object-cover"
                    />
                    <div>
                        <p className="text-sm text-blue-300 font-semibold">Verified Artist</p>
                        <h1 className="text-5xl font-bold">Marvin Gaye</h1>
                        <p className="text-sm mt-1">9,821,271 monthly listeners</p>
                    </div>
                </div>

                {/* Controls */}
                <div className="p-6 flex items-center gap-4">
                    <button className="bg-red-600 hover:bg-red-700 rounded-full p-4">
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-6 w-6"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="2"
                                d="M14.752 11.168l-6.518-3.74A1 1 0 007 8.26v7.48a1 1 0 001.234.97l6.518-1.63a1 1 0 00.748-.97v-3.21z"
                            />
                        </svg>
                    </button>
                    <button className="border border-white px-4 py-2 rounded-full text-sm hover:bg-white hover:text-black">
                        FOLLOW
                    </button>
                </div>

                {/* Popular Tracks */}
                <div className="p-6">
                    <h2 className="text-2xl font-bold mb-4">Popular</h2>

                    {/* Song List */}
                    <ul className="mt-4 space-y-4">
                        {[
                            { title: 'Freak In Me', plays: '6,146,935', duration: '03:17' },
                            { title: 'Baby One More Time', plays: '5,219,955', duration: '05:50' },
                            { title: 'Show Me How', plays: '4,911,949', duration: '02:10' },
                            { title: 'Baby Powder', plays: '4,704,616', duration: '01:32' },
                            { title: 'You & Me', plays: '3,564,078', duration: '05:10' },
                        ].map((song, i) => (
                            <li key={i} className="flex justify-between items-center">
                                <div className="flex items-center gap-4">
                                    <span className="text-zinc-400 w-5">{i + 1}</span>
                                    <img
                                        src={`https://source.unsplash.com/40x40/?music,album,cover,${i}`}
                                        alt="cover"
                                        className="w-10 h-10 object-cover"
                                    />
                                    <span>{song.title}</span>
                                </div>
                                <div className="text-zinc-400 text-sm flex gap-8">
                                    <span>{song.plays}</span>
                                    <span>{song.duration}</span>
                                </div>
                            </li>
                        ))}
                    </ul>

                    <div className="mt-4 text-sm text-red-500 cursor-pointer hover:underline">
                        SEE MORE
                    </div>
                </div>

                {/* Popular Releases */}
                <div className="p-6">
                    <h2 className="text-2xl font-bold mb-4">Popular releases</h2>
                    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
                        {Array.from({ length: 5 }).map((_, i) => (
                            <div
                                key={i}
                                className="bg-zinc-800 p-4 rounded-lg hover:bg-zinc-700 transition"
                            >
                                <img
                                    src={`https://source.unsplash.com/150x150/?album,cover,${i}`}
                                    alt="Release"
                                    className="w-full h-32 object-cover rounded-md mb-2"
                                />
                                <p className="text-sm">Album Title {i + 1}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </main>
        </div>
    );
}
