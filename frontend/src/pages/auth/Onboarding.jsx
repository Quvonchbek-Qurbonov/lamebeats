// src/pages/Onboarding.jsx
import React, {useEffect} from 'react';
import logo from '../../assets/logo.png';
import {useNavigate} from "react-router-dom";

const Onboarding = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const timer = setTimeout(() => {
            navigate("/login");
        }, 3000);

        // Cleanup timer when component unmounts
        return () => clearTimeout(timer);
    }, [navigate]);

    return (
        <div className="min-h-screen bg-black flex flex-col justify-center items-center">
            <img src={logo} alt="Lame Beats Logo" className="w-64 h-auto mb-6" />
        </div>
    );
};

export default Onboarding;
