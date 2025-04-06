// src/pages/Onboarding.jsx
import React from 'react';
import logo from '../../assets/logo.png';

const Onboarding = () => {
    return (
        <div className="min-h-screen bg-black flex flex-col justify-center items-center">
            <img src={logo} alt="Lame Beats Logo" className="w-64 h-auto mb-6" />
        </div>
    );
};

export default Onboarding;
