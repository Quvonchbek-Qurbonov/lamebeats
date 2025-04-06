// src/pages/Logout.jsx
import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Logout = () => {
    const navigate = useNavigate();

    useEffect(() => {
        localStorage.clear();
        // Redirect to login page
        navigate('/login');
    }, [navigate]);

    return null;
};

export default Logout;