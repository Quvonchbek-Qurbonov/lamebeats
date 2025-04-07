import React, { useState } from 'react';
import logo from '../../assets/logo.png';
import { useNavigate } from 'react-router-dom';

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        repeatPassword: '',
        photo: '',
    });

    const [showPassword, setShowPassword] = useState(false);
    const [showRepeatPassword, setShowRepeatPassword] = useState(false);
    const [error, setError] = useState('');

    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.repeatPassword) {
            setError("Passwords do not match.");
            return;
        }

        const avatarUrl = `https://api.dicebear.com/7.x/adventurer/svg?seed=${encodeURIComponent(formData.username)}`;

        try {
            const response = await fetch('http://localhost:8080/api/users/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: formData.username,
                    email: formData.email,
                    password: formData.password,
                    photo: avatarUrl,
                }),
            });

            const data = await response.json();

            if (!response.ok) {
                setError(data.message || 'Registration failed');
                return;
            }

            console.log('Registration successful:', data);
            navigate('/login');
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
                    {/* Username */}
                    <div>
                        <label htmlFor="username" className="block text-white text-sm mb-2">
                            Username
                        </label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            placeholder="Enter your username"
                            value={formData.username}
                            onChange={handleChange}
                            className="w-full px-4 py-2 bg-transparent border border-zinc-600 rounded-md text-white focus:outline-none focus:border-red-600"
                            required
                        />
                    </div>

                    {/* Email */}
                    <div>
                        <label htmlFor="email" className="block text-white text-sm mb-2">
                            Email
                        </label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            placeholder="Enter your email"
                            value={formData.email}
                            onChange={handleChange}
                            className="w-full px-4 py-2 bg-transparent border border-zinc-600 rounded-md text-white focus:outline-none focus:border-red-600"
                            required
                        />
                    </div>

                    {/* Password */}
                    <div>
                        <label htmlFor="password" className="block text-white text-sm mb-2">
                            Password
                        </label>
                        <div className="relative">
                            <input
                                type={showPassword ? 'text' : 'password'}
                                id="password"
                                name="password"
                                placeholder="Enter your password"
                                value={formData.password}
                                onChange={handleChange}
                                className="w-full px-4 py-2 bg-transparent border border-zinc-600 rounded-md text-white focus:outline-none focus:border-red-600"
                                required
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                className="absolute right-3 top-2.5 text-white opacity-70"
                                aria-label="Toggle password visibility"
                            >
                                {showPassword ? '🙈' : '👁️'}
                            </button>
                        </div>
                    </div>

                    {/* Repeat Password */}
                    <div>
                        <label htmlFor="repeatPassword" className="block text-white text-sm mb-2">
                            Repeat Password
                        </label>
                        <div className="relative">
                            <input
                                type={showRepeatPassword ? 'text' : 'password'}
                                id="repeatPassword"
                                name="repeatPassword"
                                placeholder="Repeat your password"
                                value={formData.repeatPassword}
                                onChange={handleChange}
                                className="w-full px-4 py-2 bg-transparent border border-zinc-600 rounded-md text-white focus:outline-none focus:border-red-600"
                                required
                            />
                            <button
                                type="button"
                                onClick={() => setShowRepeatPassword(!showRepeatPassword)}
                                className="absolute right-3 top-2.5 text-white opacity-70"
                                aria-label="Toggle repeat password visibility"
                            >
                                {showRepeatPassword ? '🙈' : '👁️'}
                            </button>
                        </div>
                    </div>

                    {/* Error Message */}
                    {error && (
                        <div className="text-red-500 text-sm text-center">
                            {error}
                        </div>
                    )}

                    {/* Register Button */}
                    <button
                        type="submit"
                        className="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded-md transition"
                    >
                        Register
                    </button>
                </form>

                {/* Divider */}
                <div className="my-6 border-t border-zinc-700"></div>

                {/* Already have an account */}
                <div className="text-center">
                    <p className="text-sm text-zinc-400">
                        Already have an account?{' '}
                        <a href="/login" className="text-red-600 hover:underline">
                            Login Now
                        </a>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;