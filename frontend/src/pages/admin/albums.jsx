import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Sidebar from "../../components/Sidebar.jsx";
import { FaPlay } from "react-icons/fa";
import PlayerBar from "../../components/player/PlayerBar.jsx";

export default function AlbumsPage() {
    const [albums, setAlbums] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetch('http://35.209.62.223/api/albums', {
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("token"),
                'Content-Type': 'application/json',
            }
        })
            .then((res) => res.json())
            .then((data) => setAlbums(data.data))
            .catch((err) => console.error(err));
    }, []);

    return (
        <div className="flex h-screen text-white bg-gradient-to-b from-red-900 to-black">
            <Sidebar/>

            <div className="p-6 overflow-y-auto flex-1">
                <h1 className="text-3xl font-bold text-white mb-4">Albums</h1>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {albums.map((album) => (
                        <div
                            key={album.id}
                            className="relative group bg-black bg-opacity-30 rounded-2xl overflow-hidden cursor-pointer shadow-md hover:shadow-lg transition-shadow"
                        >
                            <img
                                src={album.photo}
                                alt={album.title}
                                className="w-full h-60 object-cover"
                            />
                            <div className="p-4 text-white">
                                <h2 className="text-lg font-bold">{album.title}</h2>
                                <p className="text-sm text-gray-300">
                                    {album.artists.map((a) => a.name).join(', ')}
                                </p>
                            </div>
                            {/* Hover button */}
                            <button
                                onClick={() => navigate(`/admin/albums/${album.id}`)}
                                className="absolute bottom-4 right-4 p-3 bg-pink-600 text-white rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                            >
                                <FaPlay />
                            </button>
                        </div>
                    ))}
                </div>

            </div>
        </div>
    );
}
