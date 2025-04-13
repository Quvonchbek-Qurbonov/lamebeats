import React from 'react';
import {useState, useEffect} from 'react';
import {Search} from 'lucide-react';
import Sidebar from "../../components/Sidebar.jsx";

export default function SearchPage() {
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState(null);
    const [loading, setLoading] = useState(false);
    const [filters, setFilters] = useState({
        track: true,
        album: true,
        artist: true
    });
    const [error, setError] = useState(null);

    const handleSearch = async () => {
        if (!searchQuery.trim()) return;

        setLoading(true);
        setError(null);

        try {
            // Building query parameters based on filters
            const queryParams = new URLSearchParams();
            queryParams.append('query', searchQuery);

            if (filters.track) queryParams.append('track', 'true');
            if (filters.album) queryParams.append('album', 'true');
            if (filters.artist) queryParams.append('artist', 'true');

            const response = await fetch(
                `http://lamebeats.steamfest.live/api/spotify/search?${queryParams.toString()}&page=1&limit=10`,
                {
                    method: 'GET',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem("token"),
                        'Content-Type': 'application/json',
                    }
                }
            );

            if (!response.ok) {
                throw new Error('Search failed');
            }

            const data = await response.json();
            setSearchResults(data.data);
        } catch (err) {
            setError('Failed to fetch search results. Please try again.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (filterName) => {
        setFilters(prev => ({
            ...prev,
            [filterName]: !prev[filterName]
        }));
    };

    // Trigger search on Enter key
    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    // Format duration from seconds to mm:ss
    const formatDuration = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
    };

    return (
        <div className="flex h-screen text-white">
            {/* Sidebar */}
                <Sidebar/>

            {/* Main content area */}
            <div className="flex-1 overflow-hidden">
                <div className="flex flex-col h-screen bg-gradient-to-b from-red-900 to-black">
                    {/* Top search bar */}
                    <div className="flex items-center justify-between p-4 bg-red-950">
                        <div className="flex items-center w-full max-w-2xl relative">
                            <input
                                type="text"
                                placeholder="Search by Artist, Song, or Album"
                                className="w-full py-2 px-10 rounded-full bg-red-950 text-white border border-red-800 focus:outline-none focus:border-red-600"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                onKeyPress={handleKeyPress}
                            />
                            <Search className="absolute left-3 text-gray-400" size={20}/>
                        </div>
                        <button className="text-white px-6 py-2 ml-4">Admin Panel</button>
                    </div>

                    {/* Filters */}
                    <div className="flex gap-4 p-4">
                        <div className="text-white font-medium">Filter by:</div>
                        <label className="flex items-center text-white cursor-pointer">
                            <input
                                type="checkbox"
                                className="mr-2 accent-red-600 h-4 w-4"
                                checked={filters.track}
                                onChange={() => handleFilterChange('track')}
                            />
                            Songs
                        </label>
                        <label className="flex items-center text-white cursor-pointer">
                            <input
                                type="checkbox"
                                className="mr-2 accent-red-600 h-4 w-4"
                                checked={filters.album}
                                onChange={() => handleFilterChange('album')}
                            />
                            Albums
                        </label>
                        <label className="flex items-center text-white cursor-pointer">
                            <input
                                type="checkbox"
                                className="mr-2 accent-red-600 h-4 w-4"
                                checked={filters.artist}
                                onChange={() => handleFilterChange('artist')}
                            />
                            Artists
                        </label>
                        <button
                            onClick={handleSearch}
                            className="ml-auto bg-red-600 text-white px-4 py-1 rounded-full hover:bg-red-700 transition"
                        >
                            Search
                        </button>
                    </div>

                    {/* Content */}
                    <div className="flex-1 overflow-y-auto p-4">
                        {loading && (
                            <div className="flex justify-center items-center h-full">
                                <div
                                    className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-red-600"></div>
                            </div>
                        )}

                        {error && (
                            <div className="text-red-400 text-center p-4">
                                {error}
                            </div>
                        )}

                        {searchResults && !loading && (
                            <div className="space-y-8">
                                {/* Songs Section */}
                                {filters.track && searchResults.songs && searchResults.songs.length > 0 && (
                                    <div>
                                        <h2 className="text-white text-2xl font-bold mb-4">Songs</h2>
                                        <div className="grid gap-4">
                                            {searchResults.songs.map((song) => (
                                                <div key={song.spotifyId}
                                                     className="flex items-center p-2 hover:bg-red-900/40 rounded-md group">
                                                    <div
                                                        className="w-12 h-12 bg-red-800 flex-shrink-0 rounded overflow-hidden mr-4">
                                                        {song.album && song.album.images && song.album.images.length > 0 && (
                                                            <img src={song.album.images[0]} alt={song.title}
                                                                 className="w-full h-full object-cover"/>
                                                        )}
                                                    </div>
                                                    <div className="flex-1">
                                                        <h3 className="text-white font-medium">{song.title}</h3>
                                                        <p className="text-gray-400 text-sm">
                                                            {song.artists.map(artist => artist.name).join(', ')}
                                                        </p>
                                                    </div>
                                                    <div className="text-gray-400 text-sm">
                                                        {formatDuration(song.duration)}
                                                    </div>
                                                    <button
                                                        className="ml-4 bg-red-600 text-white p-2 rounded-full opacity-0 group-hover:opacity-100 transition">
                                                        ▶
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                )}

                                {/* Albums Section */}
                                {filters.album && searchResults.albums && searchResults.albums.length > 0 && (
                                    <div>
                                        <h2 className="text-white text-2xl font-bold mb-4">Albums</h2>
                                        <div
                                            className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                                            {searchResults.albums.map((album) => (
                                                <div key={album.spotifyId}
                                                     className="bg-red-900/20 p-4 rounded-md hover:bg-red-900/40 transition cursor-pointer">
                                                    <div
                                                        className="aspect-square bg-red-800 rounded overflow-hidden mb-3">
                                                        {album.images && album.images.length > 0 && (
                                                            <img src={album.images[0]} alt={album.name}
                                                                 className="w-full h-full object-cover"/>
                                                        )}
                                                    </div>
                                                    <h3 className="text-white font-medium truncate">{album.name}</h3>
                                                    <p className="text-gray-400 text-sm truncate">
                                                        {album.artists.map(artist => artist.name).join(', ')}
                                                    </p>
                                                    <p className="text-gray-500 text-xs mt-1">
                                                        {new Date(album.releaseDate).getFullYear()}
                                                    </p>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                )}

                                {/* Artists Section */}
                                {filters.artist && searchResults.artists && searchResults.artists.length > 0 && (
                                    <div>
                                        <h2 className="text-white text-2xl font-bold mb-4">Artists</h2>
                                        <div
                                            className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                                            {searchResults.artists.map((artist) => (
                                                <div key={artist.spotifyId}
                                                     className="bg-red-900/20 p-4 rounded-md hover:bg-red-900/40 transition cursor-pointer">
                                                    <div
                                                        className="aspect-square bg-red-800 rounded-full overflow-hidden mb-3 mx-auto w-4/5">
                                                        {artist.images && artist.images.length > 0 && (
                                                            <img src={artist.images[0]} alt={artist.name}
                                                                 className="w-full h-full object-cover"/>
                                                        )}
                                                    </div>
                                                    <h3 className="text-white font-medium text-center truncate">{artist.name}</h3>
                                                    <p className="text-gray-400 text-sm text-center">Artist</p>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                )}

                                {/* No results message */}
                                {(!searchResults.songs || searchResults.songs.length === 0) &&
                                    (!searchResults.albums || searchResults.albums.length === 0) &&
                                    (!searchResults.artists || searchResults.artists.length === 0) && (
                                        <div className="text-white text-center p-8">
                                            No results found for "{searchQuery}". Try a different search term or filter.
                                        </div>
                                    )}
                            </div>
                        )}

                        {!searchResults && !loading && !error && (
                            <div className="flex flex-col items-center justify-center h-full text-white">
                                <Search size={64} className="text-red-600 mb-4"/>
                                <h2 className="text-2xl font-bold mb-2">Search for music</h2>
                                <p className="text-gray-400">Find your favorite songs, albums, and artists</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}