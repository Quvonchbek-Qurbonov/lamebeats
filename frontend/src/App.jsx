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
import Albumpage from "./pages/admin/Album.jsx";

const App = () => {
    return (
        <Router>
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<Onboarding/>}/>
                <Route path="/onboarding" element={<Onboarding/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/logout" element={<Logout/>}/>
                <Route path="/album" element={<Albumpage/>}/>
                {/* TEMP Route for your dev test */}


                {/* Private Routes */}
                <Route path="/admin" element={<PrivateRoute><AdminMainPage/></PrivateRoute>}/>
                <Route path="/admin/search" element={<PrivateRoute><SearchPage/></PrivateRoute>}/>
                <Route path="/admin/artist"  element={<PrivateRoute><ArtistProfilePage/></PrivateRoute>}/>
            </Routes>
        </Router>
    );
};

export default App;
