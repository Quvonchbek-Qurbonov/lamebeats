import React from "react";

const SearchPage = () => {
    const categories = [
        { title: "Podcasts", color: "bg-green-600", image: "🎧" },
        { title: "Audiobooks", color: "bg-red-400", image: "📚" },
        { title: "Made For You", color: "bg-blue-800", image: "🎵" },
        { title: "Charts", color: "bg-purple-400", image: "📈" },
        { title: "New Releases", color: "bg-red-500", image: "🔥" },
        { title: "Discover", color: "bg-purple-500", image: "🔍" },
        { title: "Live Events", color: "bg-indigo-500", image: "🎤" },
        { title: "Hip-Hop", color: "bg-purple-300", image: "🎤" },
        { title: "Pop", color: "bg-pink-500", image: "🎶" },
        { title: "Country", color: "bg-cyan-300", image: "🤠" },
        { title: "Latin", color: "bg-pink-600", image: "💃" },
        { title: "Rock", color: "bg-lime-300", image: "🎸" },
        { title: "Summer", color: "bg-purple-300", image: "☀️" },
        { title: "Workout", color: "bg-gray-600", image: "💪" },
        { title: "R&B", color: "bg-purple-500", image: "🎙️" },
    ];

    return (
        <div className="h-screen w-screen bg-black text-white flex">
            {/* Sidebar */}
            <div className="w-60 bg-zinc-950 p-5 flex flex-col justify-between">
                <div>
                    <h1 className="text-2xl font-bold text-pink-600 mb-8">Lame <span className="text-white">beats</span></h1>
                    <nav className="space-y-4">
                        <div className="flex items-center gap-3 text-white hover:text-pink-500 cursor-pointer">
                            <span>🏠</span> <span>Home</span>
                        </div>
                        <div className="flex items-center gap-3 text-pink-500 cursor-pointer">
                            <span>🔍</span> <span>Search</span>
                        </div>
                        <div className="flex items-center gap-3 text-white hover:text-pink-500 cursor-pointer">
                            <span>📚</span> <span>Your Library</span>
                        </div>
                        <div className="flex items-center gap-3 text-white hover:text-pink-500 cursor-pointer">
                            <span>➕</span> <span>Create Playlist</span>
                        </div>
                        <div className="flex items-center gap-3 text-white hover:text-pink-500 cursor-pointer">
                            <span>💜</span> <span>Liked Songs</span>
                        </div>
                    </nav>
                </div>
                <div className="text-sm space-y-1">
                    <div className="text-gray-400">Legal</div>
                    <div className="text-gray-400">Privacy Center</div>
                    <div className="text-gray-400">Privacy Policy</div>
                    <div className="text-gray-400">Cookies</div>
                    <div className="text-gray-400">About Ads</div>
                    <button className="mt-4 px-4 py-2 border border-white rounded-full text-sm">🌐 English</button>
                </div>
            </div>

            {/* Main content */}
            <div className="flex-1 p-6 overflow-y-auto">
                <div className="flex items-center gap-4 mb-6">
                    <button className="bg-black text-white text-xl">◀</button>
                    <button className="bg-black text-white text-xl">▶</button>
                    <input
                        type="text"
                        placeholder="What do you want to listen to?"
                        className="flex-1 px-6 py-2 rounded-full bg-white text-black focus:outline-none"
                    />
                </div>

                <h2 className="text-2xl font-bold mb-4">Browse all</h2>
                <div className="grid grid-cols-5 gap-5">
                    {categories.map((item, index) => (
                        <div
                            key={index}
                            className={`rounded-lg p-4 h-40 relative overflow-hidden ${item.color} cursor-pointer transition duration-300 hover:scale-105`}
                        >
                            <h3 className="text-white font-bold text-lg z-10 relative">{item.title}</h3>
                            <div className="absolute bottom-2 right-2 text-4xl opacity-50">
                                {item.image}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default SearchPage;
