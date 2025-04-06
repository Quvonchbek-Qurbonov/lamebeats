import './App.css'
import Onboarding from "./pages/Onboarding.jsx";
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Logout from "./pages/Logout.jsx";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/onboarding" element={<Onboarding/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/logout" element={<Logout/>}/>

            </Routes>
        </Router>
    );
};

export default App
