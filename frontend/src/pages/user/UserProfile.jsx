import React, { useState } from "react";
import { Copy, Camera, Pencil } from "lucide-react";

export default function ProfilePage() {
    const [profile, setProfile] = useState({
        lamebeatsId: "1790065234B",
        name: "Meredith Johnson",
        gender: "Male",
        birthday: "06/08/1993",
        bio: "Enter your Bio",
        avatar: "https://randomuser.me/api/portraits/men/75.jpg",
    });

    const [editingField, setEditingField] = useState(null);
    const [tempValue, setTempValue] = useState("");

    const handleSave = () => {
        setProfile({ ...profile, [editingField]: tempValue });
        setEditingField(null);
        setTempValue("");
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setProfile((prev) => ({ ...prev, avatar: reader.result }));
            };
            reader.readAsDataURL(file);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-b from-neutral-900 to-black text-white flex items-center justify-center px-4">
            <div className="w-full max-w-md bg-neutral-900 rounded-xl overflow-hidden shadow-lg">
                {/* Header with Avatar */}
                <div className="bg-gradient-to-r from-red-900 to-black p-6 flex flex-col items-center">
                    <div className="relative group">
                        <img
                            src={profile.avatar}
                            alt="Profile"
                            className="w-24 h-24 rounded-2xl object-cover"
                        />
                        <label htmlFor="avatar-upload">
                            <div className="absolute bottom-0 right-0 bg-black p-1 rounded-full cursor-pointer group-hover:opacity-100 opacity-75 transition">
                                <Camera size={16} className="text-white" />
                            </div>
                        </label>
                        <input
                            id="avatar-upload"
                            type="file"
                            accept="image/*"
                            onChange={handleImageChange}
                            className="hidden"
                        />
                    </div>
                    <h2 className="mt-4 text-xl font-semibold">{profile.name}</h2>
                </div>

                {/* Profile Details */}
                <div className="divide-y divide-neutral-700">
                    <ProfileItem label="Lamebeats ID" value={profile.lamebeatsId} icon={<Copy size={16} />} readOnly />
                    <ProfileItem label="Name" value={profile.name} onEdit={() => {
                        setEditingField("name");
                        setTempValue(profile.name);
                    }} />
                    <ProfileItem label="Gender" value={profile.gender} onEdit={() => {
                        setEditingField("gender");
                        setTempValue(profile.gender);
                    }} />
                    <ProfileItem label="Birthday" value={profile.birthday} onEdit={() => {
                        setEditingField("birthday");
                        setTempValue(profile.birthday);
                    }} />
                    <ProfileItem label="Bio" value={profile.bio} onEdit={() => {
                        setEditingField("bio");
                        setTempValue(profile.bio);
                    }} />
                </div>
            </div>

            {/* Edit Modal */}
            {editingField && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-neutral-800 rounded-xl p-6 w-80">
                        <h3 className="text-lg font-semibold mb-4">Edit {editingField}</h3>
                        <input
                            type="text"
                            value={tempValue}
                            onChange={(e) => setTempValue(e.target.value)}
                            className="w-full px-3 py-2 rounded bg-neutral-700 text-white focus:outline-none"
                        />
                        <div className="flex justify-end gap-2 mt-4">
                            <button
                                onClick={() => setEditingField(null)}
                                className="px-4 py-1 bg-neutral-600 rounded hover:bg-neutral-500"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleSave}
                                className="px-4 py-1 bg-[#e0032f] rounded hover:bg-[#c30229]"
                            >
                                Save
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

function ProfileItem({ label, value, onEdit, icon, readOnly = false }) {
    return (
        <div className="flex justify-between items-center px-6 py-4 hover:bg-neutral-800 transition">
            <div>
                <p className="text-sm text-neutral-400 mb-1">{label}</p>
                <p className="text-sm">{value}</p>
            </div>
            {readOnly ? (
                <div className="ml-2 text-neutral-400">{icon}</div>
            ) : (
                <button onClick={onEdit} className="ml-2 text-neutral-400 hover:text-white">
                    <Pencil size={16} />
                </button>
            )}
        </div>
    );
}
