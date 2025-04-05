import './App.css'
import Onboarding from "./pages/Onboarding.jsx";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/onboarding" element={<Onboarding />} />
                {/* Add other routes here if needed */}
            </Routes>
        </Router>
    );
};

export default App
