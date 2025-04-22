import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { FaGoogle, FaFacebookF, FaEye, FaEyeSlash } from "react-icons/fa";
import logo from "/src/assets/logo.png";

export default function AuthTransitionUI() {
    const [isSignUp, setIsSignUp] = useState(false);
    const [signInEmail, setSignInEmail] = useState("");
    const [signInPassword, setSignInPassword] = useState("");
    const [signUpName, setSignUpName] = useState("");
    const [signUpEmail, setSignUpEmail] = useState("");
    const [signUpPassword, setSignUpPassword] = useState("");
    const [signUpRepeatPassword, setSignUpRepeatPassword] = useState("");
    const [error, setError] = useState("");

    const [showSignInPassword, setShowSignInPassword] = useState(false);
    const [showSignUpPassword, setShowSignUpPassword] = useState(false);
    const [showRepeatPassword, setShowRepeatPassword] = useState(false);

    const navigate = useNavigate();

    const handleSignIn = async () => {
        setError("");
        try {
            const response = await fetch("http://35.209.62.223/api/users/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: signInEmail, password: signInPassword }),
            });

            const data = await response.json();
            if (!response.ok) {
                setError(data.message || "Login failed");
                return;
            }

            localStorage.setItem("token", data.token);
            localStorage.setItem("userType", data.userType);
            localStorage.setItem("userId", data.userId);
            localStorage.setItem("tokenTtl", data.tokenTtl);
            localStorage.setItem("validUntil", data.validUntil);

            navigate("/admin");
        } catch (err) {
            setError("Something went wrong. Please try again.");
        }
    };

    const handleSignUp = async () => {
        setError("");
        if (signUpPassword !== signUpRepeatPassword) {
            setError("Passwords do not match.");
            return;
        }

        const avatarUrl = `https://api.dicebear.com/7.x/adventurer/svg?seed=${encodeURIComponent(signUpName)}`;

        try {
            const response = await fetch("http://35.209.62.223/api/users/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username: signUpName,
                    email: signUpEmail,
                    password: signUpPassword,
                    photo: avatarUrl,
                }),
            });

            const data = await response.json();
            if (!response.ok) {
                setError(data.message || "Registration failed");
                return;
            }

            setIsSignUp(false);
        } catch (err) {
            setError("Something went wrong. Please try again.");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-b from-[#040404] to-[#292929] font-sans">
            <div className="relative w-full max-w-[900px] h-[550px] rounded-3xl overflow-hidden bg-black shadow-2xl">
                {/* Sign In Form */}
                <motion.div
                    className="absolute top-0 left-0 w-1/2 h-full text-white p-10 flex flex-col justify-center items-center z-20 text-center"
                    animate={{
                        x: isSignUp ? "-100%" : "0%",
                        opacity: isSignUp ? 0 : 1,
                        pointerEvents: isSignUp ? "none" : "auto",
                    }}
                    transition={{ duration: 0.3, delay: isSignUp ? 0.1 : 0.3 }}
                >
                    <div className="w-full max-w-sm">
                        <img src={logo} alt="Lame Beats Logo" className="w-32 mx-auto mb-4" />
                        <h2 className="text-2xl font-semibold mb-4">Sign In</h2>
                        <div className="flex gap-3 mb-4 justify-center">
                            <button className="w-8 h-8 border rounded-full flex items-center justify-center"><FaGoogle /></button>
                            <button className="w-8 h-8 border rounded-full flex items-center justify-center"><FaFacebookF /></button>
                        </div>
                        <p className="text-xs mb-4">or use your email password</p>
                        <input
                            className="mb-3 p-2 w-full rounded bg-gray-800 border border-gray-700 text-sm"
                            type="text"
                            placeholder="Username or Email"
                            value={signInEmail}
                            onChange={(e) => setSignInEmail(e.target.value)}
                        />
                        <div className="relative mb-3">
                            <input
                                className="p-2 pr-10 w-full rounded bg-gray-800 border border-gray-700 text-sm"
                                type={showSignInPassword ? "text" : "password"}
                                placeholder="Password"
                                value={signInPassword}
                                onChange={(e) => setSignInPassword(e.target.value)}
                            />
                            <button
                                type="button"
                                onClick={() => setShowSignInPassword(!showSignInPassword)}
                                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white"
                            >
                                {showSignInPassword ? <FaEyeSlash /> : <FaEye />}
                            </button>
                        </div>
                        <button
                            onClick={() => alert("Redirect to forgot password page")}
                            className="text-xs text-gray-400 mb-2 hover:underline"
                        >
                            Forgot Your Password?
                        </button>
                        {error && !isSignUp && (
                            <div className="text-red-500 text-sm mb-2">{error}</div>
                        )}
                        <button
                            onClick={handleSignIn}
                            className="mt-2 w-full bg-red-700 hover:bg-red-800 text-white py-2 rounded"
                        >
                            Sign In
                        </button>
                    </div>
                </motion.div>

                {/* Sign Up Form */}
                <motion.div
                    className="absolute top-0 right-0 w-1/2 h-full text-white p-10 flex flex-col justify-center items-center z-20 text-center"
                    animate={{
                        x: isSignUp ? "0%" : "100%",
                        opacity: isSignUp ? 1 : 0,
                        pointerEvents: isSignUp ? "auto" : "none",
                    }}
                    transition={{ duration: 0.3, delay: isSignUp ? 0.3 : 0.1 }}
                >
                    <div className="w-full max-w-sm">
                        <img src={logo} alt="Lame Beats Logo" className="w-32 mx-auto mb-4" />
                        <h2 className="text-2xl font-semibold mb-4">Create Account</h2>
                        <div className="flex gap-3 mb-4 justify-center">
                            <button className="w-8 h-8 border rounded-full flex items-center justify-center"><FaGoogle /></button>
                            <button className="w-8 h-8 border rounded-full flex items-center justify-center"><FaFacebookF /></button>
                        </div>
                        <p className="text-xs mb-4">or use your email for registration</p>
                        <input
                            className="mb-3 p-2 w-full rounded bg-gray-800 border border-gray-700 text-sm"
                            type="text"
                            placeholder="Username"
                            value={signUpName}
                            onChange={(e) => setSignUpName(e.target.value)}
                        />
                        <input
                            className="mb-3 p-2 w-full rounded bg-gray-800 border border-gray-700 text-sm"
                            type="email"
                            placeholder="Email"
                            value={signUpEmail}
                            onChange={(e) => setSignUpEmail(e.target.value)}
                        />
                        <div className="relative mb-3">
                            <input
                                className="p-2 pr-10 w-full rounded bg-gray-800 border border-gray-700 text-sm"
                                type={showSignUpPassword ? "text" : "password"}
                                placeholder="Password"
                                value={signUpPassword}
                                onChange={(e) => setSignUpPassword(e.target.value)}
                            />
                            <button
                                type="button"
                                onClick={() => setShowSignUpPassword(!showSignUpPassword)}
                                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white"
                            >
                                {showSignUpPassword ? <FaEyeSlash /> : <FaEye />}
                            </button>
                        </div>
                        <div className="relative mb-3">
                            <input
                                className="p-2 pr-10 w-full rounded bg-gray-800 border border-gray-700 text-sm"
                                type={showRepeatPassword ? "text" : "password"}
                                placeholder="Repeat Password"
                                value={signUpRepeatPassword}
                                onChange={(e) => setSignUpRepeatPassword(e.target.value)}
                            />
                            <button
                                type="button"
                                onClick={() => setShowRepeatPassword(!showRepeatPassword)}
                                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white"
                            >
                                {showRepeatPassword ? <FaEyeSlash /> : <FaEye />}
                            </button>
                        </div>
                        {error && isSignUp && (
                            <div className="text-red-500 text-sm mb-2">{error}</div>
                        )}
                        <button
                            onClick={handleSignUp}
                            className="mt-2 w-full bg-red-700 hover:bg-red-800 text-white py-2 rounded"
                        >
                            Sign Up
                        </button>
                    </div>
                </motion.div>

                {/* Sliding Panel */}
                <motion.div
                    className={`absolute top-0 bottom-0 w-1/2 z-10 text-white p-10 flex flex-col justify-center items-center transition-all duration-300 ${
                        isSignUp ? "left-0 rounded-r-[150px]" : "right-0 rounded-l-[150px]"
                    }`}
                    style={{ background: "linear-gradient(to bottom, #ff0000, #8b0000)" }}
                >
                    <motion.div
                        animate={{ scaleX: isSignUp ? -1 : 1 }}
                        className="w-full h-full flex flex-col justify-center items-center"
                    >
                        <div className="text-center px-4" style={{ transform: isSignUp ? "scaleX(-1)" : "scaleX(1)" }}>
                            <h2 className="text-3xl font-bold mb-4">
                                {isSignUp ? "Welcome Back!" : "Hello, Friend!"}
                            </h2>
                            <p className="text-sm mb-6 text-center max-w-xs">
                                {isSignUp
                                    ? "Enter your personal details to use all of site features"
                                    : "Register with your personal details to use all of site features"}
                            </p>
                            <button
                                onClick={() => {
                                    setError("");
                                    setIsSignUp(!isSignUp);
                                }}
                                className="px-6 py-2 border border-white text-white rounded-full hover:bg-white hover:text-red-700 transition"
                            >
                                {isSignUp ? "Sign In" : "Sign Up"}
                            </button>
                        </div>
                    </motion.div>
                </motion.div>
            </div>
        </div>
    );
}
