import React from "react";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import Onboarding from "./pages/auth/Onboarding.jsx";
import Register from "./pages/auth/Register.jsx";
import Login from "./pages/auth/Login.jsx";
import Logout from "./pages/auth/Logout.jsx";
import AdminMainPage from "./pages/admin/AdminMainPage.jsx";
import PrivateRoute from "./components/PrivateRoute.jsx";

const App = () => {
    return (
        <Router>
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<Onboarding />} />
                <Route path="/onboarding" element={<Onboarding />} />
                <Route path="/register" element={<Register />} />
                <Route path="/login" element={<Login />} />
                <Route path="/logout" element={<Logout />} />

                {/* Private Routes */}
                <Route
                    path="/admin"
                   element={
                    <PrivateRoute>
                        <AdminMainPage />
                    </PrivateRoute>
                   }
                />
            </Routes>
        </Router>
    );
};

export default App
