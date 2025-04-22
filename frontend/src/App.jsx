import React from "react";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import SearchPage from "./pages/admin/SearchPage.jsx";
import Onboarding from "./pages/auth/Onboarding.jsx";
import Register from "./pages/auth/Register.jsx";
import Login from "./pages/auth/Login.jsx";
import Logout from "./pages/auth/Logout.jsx";
import AdminMainPage from "./pages/admin/AdminMainPage.jsx";
import PrivateRoute from "./components/PrivateRoute.jsx";
import ArtistProfilePage from "./pages/admin/ArtistProfile.jsx";
import { MusicPlayerProvider } from "./context/MusicPlayerContext.jsx";
import PlayerBar from "./components/player/PlayerBar.jsx";
import AlbumPage from "./pages/admin/Album.jsx";
import AlbumsPage from "./pages/admin/Albums.jsx";

const App = () => {
    return (
        <MusicPlayerProvider>
            <Router>
                <Routes>
                    {/* Public Routes */}
                    <Route path="/" element={<Onboarding/>}/>
                    <Route path="/onboarding" element={<Onboarding/>}/>
                    <Route path="/register" element={<Register/>}/>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/logout" element={<Logout/>}/>


                    {/* Private Routes */}
                    <Route path="/admin" element={<PrivateRoute><AdminMainPage/></PrivateRoute>}/>
                    <Route path="/admin/search" element={<PrivateRoute><SearchPage/></PrivateRoute>}/>
                    <Route path="/admin/artist"  element={<PrivateRoute><ArtistProfilePage/></PrivateRoute>}/>
                    <Route path="/admin/albums" element={<PrivateRoute><AlbumsPage/></PrivateRoute>}/>
                    <Route path="/admin/albums/:id" element={<PrivateRoute><AlbumPage/></PrivateRoute>}/>
                </Routes>

                {/* Global Player Bar that will appear when music is playing */}
                <PlayerBar />
            </Router>
        </MusicPlayerProvider>
    );
};

export default App;