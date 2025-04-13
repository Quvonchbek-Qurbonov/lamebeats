import React, { useState, useEffect } from "react";
import { Search, Home, Library, Plus, Heart, PersonStanding, Disc3, Speech, Music2 } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function AdminMainPage() {
    const navigate = useNavigate();
    const [songs, setSongs] = useState([]);
    const [albums, setAlbums] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchSongs = async () => {
            try {
                setLoading(true);
                const response = await fetch('http://lamebeats.steamfest.live/api/songs?limit=5', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem("token"),
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch songs data');
                }

                const data = await response.json();
                setSongs(data.data || []);
            } catch (err) {
                setError('Failed to load data. Please try again later.');
                console.error('Error fetching songs:', err);
            } finally {
                setLoading(false);
            }
        };

        const fetchAlbums = async () => {
            try {
                setLoading(true);
                const response = await fetch('http://lamebeats.steamfest.live/api/albums?limit=5', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem("token"),
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch songs data');
                }

                const data = await response.json();
                setAlbums(data.data || []);
            } catch (err) {
                setError('Failed to load data. Please try again later.');
                console.error('Error fetching songs:', err);
            } finally {
                setLoading(false);
            }
        };


        fetchSongs();
        fetchAlbums();
    }, []);

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
                            <Search size={18} /> External Search
                        </a>
                        <a href="#library" className="flex items-center gap-3 hover:text-red-400">
                            <Library size={18} /> System Library
                        </a>
                    </nav>

                    <div className="mt-10 space-y-4 text-sm">
                        <a href="#" className="flex items-center gap-3 hover:text-red-400">
                            <Speech size={18} /> Artists
                        </a>
                        <a href="#" className="flex items-center gap-3 hover:text-red-400">
                            <Disc3 size={18} /> Albums
                        </a>
                        <a href="#" className="flex items-center gap-3 hover:text-red-400">
                            <Music2 size={18} /> Songs
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
                            className="bg-red-900/70 text-white px-4 py-2 rounded-full w-full max-w-md placeholder:text-red-200 focus:outline-none border border-red-400/30"
                        />
                        <div className="ml-4 bg-red-800/80 px-4 py-2 rounded-full font-semibold">Admin Panel</div>
                    </header>

                    {/* Error/Loading States */}
                    {error && (
                        <div className="bg-red-900/50 p-4 rounded-lg mb-5 text-center">
                            {error}
                        </div>
                    )}

                    {loading ? (
                        <div className="flex justify-center items-center h-40">
                            <div className="animate-pulse text-red-400">Loading music data...</div>
                        </div>
                    ) : (
                        <>
                            {/* Songs Section */}
                            <section className="mb-10">
                                <h2 className="text-2xl font-bold mb-4">Latest Songs</h2>
                                <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-5">
                                    {songs.length > 0 ? songs.map((song) => (
                                        <div key={song.id} className="bg-neutral-900 p-4 rounded-lg hover:bg-neutral-800 transition">
                                            <div className="aspect-square bg-red-700 rounded mb-3 overflow-hidden">
                                                <img
                                                    src={song.album.photo}
                                                    alt={song.title}
                                                    className="w-full h-full object-cover"
                                                />
                                            </div>
                                            <h3 className="font-semibold truncate">{song.title}</h3>
                                            <p className="text-sm opacity-70 truncate">
                                                {song.artists.map(artist => artist.name).join(', ')}
                                            </p>
                                        </div>
                                    )) : (
                                        <div className="col-span-5 text-center py-10">
                                            No songs available
                                        </div>
                                    )}
                                </div>
                            </section>

                            {/* Albums Section */}
                            <section>
                                <h2 className="text-2xl font-bold mb-4">Albums</h2>
                                <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-5">
                                    {Object.values(albums).length > 0 ? Object.values(albums).map((album) => (
                                        <div key={album.id} className="bg-neutral-900 p-4 rounded-lg hover:bg-neutral-800 transition">
                                            <div className="aspect-square bg-red-700 rounded mb-3 overflow-hidden">
                                                <img
                                                    src={album.photo}
                                                    alt={album.title}
                                                    className="w-full h-full object-cover"
                                                />
                                            </div>
                                            <h3 className="font-semibold truncate">{album.title}</h3>
                                            <p className="text-sm opacity-70">
                                                Released: {new Date(album.releaseDate).getFullYear()}
                                            </p>
                                        </div>
                                    )) : (
                                        <div className="col-span-5 text-center py-10">
                                            No albums available
                                        </div>
                                    )}
                                </div>
                            </section>
                        </>
                    )}
                </div>
            </main>
        </div>
    );
}