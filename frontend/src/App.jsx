import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ShopPage from "./pages/ShopPage"; // Import ShopPage
import { Toaster } from "react-hot-toast";
import { AuthProvider } from "./context/Context";

function App() {
  return (
    <AuthProvider>
      <Toaster position="bottom-right" />
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/shop/:id" element={<ShopPage />} /> {/* Add ShopPage route */}
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
