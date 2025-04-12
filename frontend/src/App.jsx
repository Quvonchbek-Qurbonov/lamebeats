import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import SearchPage from "./pages/admin/Search-page.jsx"; // 👈 Make sure this path is correct

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="*" element={<SearchPage />} />
            </Routes>
        </Router>
    );
};

export default App;
