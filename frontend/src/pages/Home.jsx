import React from "react";
import "../css/Home.css";
import { useAuth } from "../context/Context";
import ShopCard from "../components/ShopCard";
import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function Home() {
  const navigate = useNavigate();
  const { user, token, logout } = useAuth();
  const [shopList, setShopList] = useState([]);

  useEffect(() => {
    async function fetchShops() {
      const res = await axios.get("http://localhost:8080/api/shops");
      console.log(res);
      setShopList(res.data);
    }

    fetchShops();
  }, []);

  return (
    <div className="home-container">
      <h1>Appointment Booker</h1>
      <div>
        <div className="user-info">
          {user ? (
            <>
              <h2>Welcome, {user.name}!</h2>
              {user.role === "host" && (
                <button onClick={() => navigate("/shops/create")}>
                  Create shop
                </button>
              )}
              <button onClick={logout}>Logout</button>
            </>
          ) : (
            <>
              <h2>Browse Available Services</h2>
            </>
          )}
        </div>
        <div className="shops-list">
          {shopList.map((shop) => (
            <ShopCard key={shop.id} shop={shop} />
          ))}
        </div>
      </div>
    </div>
  );
}
