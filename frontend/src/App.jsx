import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ShopPage from "./pages/ShopPage"; // Import ShopPage
import { Toaster } from "react-hot-toast";
import { AuthProvider } from "./context/Context";
import CreateShop from "./pages/CreateShop";
import ManageShops from "./pages/ManageShops";
import EditShop from "./pages/EditShop";
import EditShopServices from "./pages/EditShopServices";
import ManageBookings from "./pages/ManageBookings";

function App() {
  return (
    <AuthProvider>
      <Toaster position="bottom-right" />
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/shops/create" element={<CreateShop />} />
          <Route path="/shops/manage" element={<ManageShops />} />
          <Route path="/shops/:id/edit" element={<EditShop />} />
          <Route path="/shops/:id" element={<ShopPage />} />
          <Route
            path="/shops/:id/services/edit"
            element={<EditShopServices />}
          />
          <Route path="/bookings/manage" element={<ManageBookings />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
