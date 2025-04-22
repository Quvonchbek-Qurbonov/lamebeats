import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { ChevronLeft, ChevronRight, ChevronDown, Share, Pause, Play } from "lucide-react";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import Sidebar from "../../components/Sidebar";
import { useMusicPlayer } from '../../context/MusicPlayerContext'; // ✅ Your hook path

export default function AlbumPage() {
    const { id } = useParams();
    const [album, setAlbum] = useState(null);
    const [isLiked, setIsLiked] = useState(false);

    const {
        playSong,
        pause,
        isPlaying,
        currentSong,
    } = useMusicPlayer(); // ✅ Using context

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("No token found in localStorage");
            return;
        }

        fetch(`http://35.209.62.223/api/albums/${id}`, {
            headers: {
                Authorization: 'Bearer ' + token,
                'Content-Type': 'application/json',
            },
        })
            .then(res => {
                if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
                return res.json();
            })
            .then(albumData => {
                if (albumData?.id && albumData?.title) {
                    setAlbum(albumData);
                } else {
                    console.error("Invalid album data", albumData);
                }
            })
            .catch(err => console.error("Error fetching album:", err));
    }, [id]);

    const formatDuration = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${String(secs).padStart(2, '0')}`;
    };

    if (!album) return <div className="text-white p-6">Loading...</div>;

    const artists = Array.isArray(album.artists) ? album.artists : [];
    const songs = Array.isArray(album.songs) ? album.songs : [];

    const releaseYear = album.releaseDate ? new Date(album.releaseDate).getFullYear() : "N/A";
    const songCount = album.songCount || songs.length;

    return (
        <div className="flex h-screen text-white bg-black">
            <Sidebar />

            <main className="flex-1 bg-gradient-to-b from-blue-900 to-black p-8 overflow-y-auto">
                <div className="flex justify-between items-center w-full">
                    <div className="flex space-x-2">
                        <button className="hover:text-rose-500"><ChevronLeft /></button>
                        <button className="hover:text-rose-500"><ChevronRight /></button>
                    </div>
                    <button className="flex items-center hover:text-rose-500">
                        <img
                            src={artists?.[0]?.photo || "https://via.placeholder.com/36"}
                            alt="artist"
                            className="rounded-full h-[36px] w-[36px] object-cover mr-2"
                        />
                        {artists?.[0]?.name || "Artist"} <ChevronDown />
                    </button>
                </div>

                <div className="mt-6 flex items-end space-x-6">
                    <img src={album.photo} alt="Album Cover" className="w-48 h-48 object-cover rounded" />
                    <div>
                        <p className="text-sm uppercase">Album</p>
                        <h1 className="text-6xl font-bold">{album.title}</h1>
                        <p className="text-sm mt-2">
                            {artists.map(artist => artist.name).join(', ')} • {releaseYear} • {songCount} song{songCount > 1 ? 's' : ''}
                        </p>
                    </div>
                </div>

                <div className="mt-6 flex items-center space-x-4">
                    <button
                        onClick={() => {
                            if (isPlaying) pause();
                            else if (currentSong) playSong(currentSong);
                        }}
                        className="bg-pink-600 p-4 rounded-full hover:bg-pink-700"
                    >
                        {isPlaying ? <Pause size={24} /> : <Play size={24} />}
                    </button>
                    <button onClick={() => setIsLiked(!isLiked)}>
                        {isLiked ? <FaHeart className="text-pink-500" size={22} /> : <FaRegHeart className="hover:text-pink-500" size={22} />}
                    </button>
                    <Share className="hover:text-pink-500" />
                </div>

                <div className="mt-8">
                    <div className="grid grid-cols-4 text-zinc-400 text-sm border-b border-zinc-700 pb-2">
                        <span>Title</span>
                        <span>Album</span>
                        <span>Plays</span>
                        <span>Time</span>
                    </div>

                    {songs.length ? (
                        songs.map((song, i) => (
                            <div key={i} className="grid grid-cols-4 items-center py-2 border-b border-zinc-800 hover:bg-zinc-900 group px-2">
                                <div className="flex items-center gap-3">
                                    <button
                                        onClick={() => playSong(song)}
                                        className="text-zinc-400 hover:text-white transition"
                                    >
                                        <Play size={18} />
                                    </button>
                                    <span className="text-white">{song.title}</span>
                                </div>
                                <span className="text-zinc-400">{album.title}</span>
                                <span className="text-zinc-400">{song.plays ?? "N/A"}</span>
                                <span className="text-zinc-400">{formatDuration(song.duration || 0)}</span>
                            </div>
                        ))
                    ) : (
                        <div className="text-white">No songs available.</div>
                    )}
                </div>
            </main>
        </div>
    );
}
