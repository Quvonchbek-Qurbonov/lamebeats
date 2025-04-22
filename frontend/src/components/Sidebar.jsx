import { Disc3, Home, Library, Music2, Search, Speech } from "lucide-react";
import React from "react";
import { useNavigate } from "react-router-dom";

export default function Sidebar() {
    const navigate = useNavigate();

    const handleLogout = () => {
        navigate('/logout');
    };

    return (
        <aside className="w-60 bg-black p-6 flex flex-col justify-between">
            <div>
                <h1 className="text-2xl font-bold text-red-500 mb-10">LameBeats</h1>
                <nav className="space-y-4 text-sm">
                    <button
                        onClick={() => navigate('/admin')}
                        className="flex items-center gap-3 hover:text-red-400"
                    >
                        <Home size={18}/> Home
                    </button>
                    <button
                        onClick={() => navigate('/admin/search')}
                        className="flex items-center gap-3 hover:text-red-400"
                    >
                        <Search size={18}/> External Search
                    </button>
                    <button
                        onClick={() => navigate('/admin/library')}
                        className="flex items-center gap-3 hover:text-red-400"
                    >
                        <Library size={18}/> System Library
                    </button>
                </nav>

                <div className="mt-10 space-y-4 text-sm">
                    <button  onClick={() => navigate('/admin/artists')} className="flex items-center gap-3 hover:text-red-400">
                        <Speech size={18}/> Artists
                    </button>
                    <button  onClick={() => navigate('/admin/albums')} className="flex items-center gap-3 hover:text-red-400">
                        <Disc3 size={18}/> Albums
                    </button>
                    <button onClick={() => navigate('/admin/Songs')} className="flex items-center gap-3 hover:text-red-400">
                        <Music2 size={18}/> Songs
                    </button>
                </div>
            </div>

            <div className="text-xs text-gray-400 space-y-1">
                <button
                    onClick={handleLogout}
                    className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-full transition duration-300 ease-in-out">
                    Logout
                </button>
            </div>
        </aside>
    );
}