import React from 'react';

const SearchResult = () => {
    const tracks = [
        { id: 1, title: 'Freak In Me', artist: 'Artist Name', album: 'Album Name', time: '03:17', img: 'https://via.placeholder.com/50' },
        { id: 2, title: 'Baby One More Time', artist: 'Artist Name', album: 'Album Name', time: '05:50', img: 'https://via.placeholder.com/50' },
        { id: 3, title: 'Show Me How', artist: 'Artist Name', album: 'Album Name', time: '02:10', img: 'https://via.placeholder.com/50' },
        { id: 4, title: 'Baby Powder', artist: 'Artist Name', album: 'Album Name', time: '01:32', img: 'https://via.placeholder.com/50' },
        { id: 5, title: 'You & Me', artist: 'Artist Name', album: 'Album Name', time: '05:10', img: 'https://via.placeholder.com/50' },
    ];

    return (
        <div className="flex h-screen text-white bg-black">
            {/* Sidebar */}
            <aside className="w-60 bg-zinc-900 p-4 flex flex-col justify-between">
                <div>
                    <ul className="space-y-4">
                        <li className="flex items-center gap-2 text-white"><span>🏠</span> <span>Home</span></li>
                        <li className="flex items-center gap-2 text-red-500 font-bold"><span>🔍</span> <span>Search</span></li>
                        <li className="flex items-center gap-2 text-white"><span>📚</span> <span>Your Library</span></li>
                        <li className="flex items-center gap-2 text-white mt-6"><span>➕</span> <span>Create Playlist</span></li>
                        <li className="flex items-center gap-2 text-white"><span>💜</span> <span>Liked Songs</span></li>
                    </ul>
                </div>
                <div className="text-xs text-gray-400 space-y-1">
                    <p>Legal | Privacy Center</p>
                    <p>Cookies</p>
                </div>
            </aside>

            {/* Main content */}
            <main className="flex-1 p-6 bg-neutral-900">
                {/* Search Header */}
                <div className="bg-red-700 p-4 rounded-md mb-6">
                    <div className="flex items-center justify-between mb-2">
                        <input
                            type="text"
                            placeholder="Artists, songs, or album"
                            className="w-2/3 px-4 py-2 rounded-full text-black bg-white"
                        />
                        <div className="text-sm font-medium">Cody Fisher ⬇️</div>
                    </div>
                    <div className="flex space-x-4 text-sm">
                        <label className="flex items-center gap-1">
                            <input type="checkbox" defaultChecked />
                            by Artist
                        </label>
                        <label className="flex items-center gap-1">
                            <input type="checkbox" defaultChecked />
                            by Title
                        </label>
                        <label className="flex items-center gap-1">
                            <input type="checkbox" defaultChecked />
                            by Album
                        </label>
                    </div>
                </div>

                {/* Results Table */}
                <div>
                    <h2 className="text-xl font-semibold mb-4">Results</h2>
                    <table className="w-full table-auto text-left">
                        <thead>
                        <tr className="text-gray-400 border-b border-gray-700">
                            <th className="py-2">#</th>
                            <th className="py-2">Title</th>
                            <th className="py-2">Artist</th>
                            <th className="py-2">Album</th>
                            <th className="py-2">Time</th>
                            <th className="py-2"></th>
                        </tr>
                        </thead>
                        <tbody>
                        {tracks.map((track) => (
                            <tr key={track.id} className="border-b border-gray-800 hover:bg-zinc-800">
                                <td className="py-3">{track.id}</td>
                                <td className="flex items-center gap-3 py-3">
                                    <img src={track.img} alt="" className="w-10 h-10 rounded-md" />
                                    {track.title}
                                </td>
                                <td className="py-3">{track.artist}</td>
                                <td className="py-3">{track.album}</td>
                                <td className="py-3">{track.time}</td>
                                <td className="py-3">
                                    <button className="bg-red-600 hover:bg-red-700 px-4 py-1 rounded-md">Add</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </main>
        </div>
    );
};

export default SearchResult;
