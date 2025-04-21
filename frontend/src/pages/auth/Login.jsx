import React, { useState } from 'react';
import logo from '../../assets/logo.png';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });

    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://lamebeats.steamfest.live/api/users/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData),
            });

            const data = await response.json();

            if (!response.ok) {
                setError(data.message || 'Login failed');
                return;
            }

            // Store auth data in localStorage
            localStorage.setItem('token', data.token);
            localStorage.setItem('userType', data.userType);
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('tokenTtl', data.tokenTtl);
            localStorage.setItem('validUntil', data.validUntil);

            navigate('/admin');
        } catch (err) {
            setError('Something went wrong. Please try again.');

        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-b from-black to-zinc-900 flex justify-center items-center px-4">
            <div className="w-full max-w-md bg-[#111] p-8 rounded-lg shadow-md">
                <div className="flex justify-center mb-6">
                    <img src={logo} alt="Lame Beats" className="w-32 h-auto" />
                </div>

                <form className="space-y-5" onSubmit={handleSubmit}>
                    {/* Email/Username Input */}
                    <div>
                        <label className="block text-white text-sm mb-2">
                            Username
                        </label>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            placeholder="Enter your username or email"
                            className="w-full px-4 py-2 bg-transparent border border-zinc-600 rounded-md text-white focus:outline-none focus:border-red-600"
                            required
                        />
                    </div>

                    {/* Password Input */}
                    <div>
                        <label className="block text-white text-sm mb-2">Password</label>
                        <div className="relative">
                            <input
                                type={showPassword ? 'text' : 'password'}
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="************"
                                className="w-full px-4 py-2 bg-transparent border border-zinc-600 rounded-md text-white focus:outline-none focus:border-red-600"
                                required
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                className="absolute right-3 top-2.5 text-white opacity-70"
                            >
                                {showPassword ? '🙈' : '👁️'}
                            </button>
                        </div>
                    </div>

                    {/* Error Message */}
                    {error && (
                        <div className="text-red-500 text-sm text-center">
                            {error}
                        </div>
                    )}

                    {/* Login Button */}
                    <button
                        type="submit"
                        className="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded-md transition"
                    >
                        Login
                    </button>
                </form>

                {/* Divider */}
                <div className="my-6 border-t border-zinc-700"></div>

                <div className="text-center">
                    <p className="text-m text-zinc-400">
                        Don't have an account?{' '}
                        <a href="/register" className="text-red-600 hover:underline">
                            Register
                        </a>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;
