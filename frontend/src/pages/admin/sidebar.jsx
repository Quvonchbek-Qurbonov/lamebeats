import React from "react";
import { Link } from "react-router-dom";
import { Home, Search, Library, Plus } from "lucide-react";

export default function Sidebar() {
    const playlists = [
        "Running Playlist",
        "21st Birthday",
        "April, 2023",
        "Gym Session",
        "Classic Anthems",
        "R&B Favourites",
        "Classical Music",
        "Hayleys Bday",
        "Discover Weekly",
        "Liked From Radio",
    ];

    return (
        <aside className="w-64 bg-zinc-900 p-4">
            <nav className="space-y-4">
                <Link
                    to="#"
                    className="flex items-center space-x-2 hover:text-pink-500 transition-colors"
                >
                    <Home size={20} /> <span>Home</span>
                </Link>
                <Link
                    to="#"
                    className="flex items-center space-x-2 hover:text-pink-500 transition-colors"
                >
                    <Search size={20} /> <span>Search</span>
                </Link>
                <Link
                    to="#"
                    className="flex items-center space-x-2 hover:text-pink-500 transition-colors"
                >
                    <Library size={20} /> <span>Your Library</span>
                </Link>
                <Link
                    to="#"
                    className="flex items-center space-x-2 hover:text-pink-500 transition-colors"
                >
                    <Plus size={20} /> <span>Create Playlist</span>
                </Link>
                <Link to="#" className="text-pink-500 hover:underline">
                    Liked Songs
                </Link>

                <div className="mt-6 space-y-2 text-sm text-zinc-400">
                    {playlists.map((title) => (
                        <Link
                            key={title}
                            to="#"
                            className="block hover:text-pink-500 transition-colors"
                        >
                            {title}
                        </Link>
                    ))}
                </div>
            </nav>
        </aside>
    );
}
