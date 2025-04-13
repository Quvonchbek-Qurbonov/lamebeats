import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import SearchResult from "./pages/admin/search-result.jsx";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<SearchResult />} />
            </Routes>
        </Router>
    );
}

export default App;
