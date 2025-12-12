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
      <div className="user-info">
        {user ? (
          <>
            <h2>Welcome, {user.name}!</h2>
            {user.role === "host" && (
              <div>
                <button onClick={() => navigate("/shops/manage")}>
                  Manage
                </button>
              </div>
            )}
            <button onClick={logout}>Logout</button>
          </>
        ) : (
          <>
            <h2>Apt Booker</h2>
            <div>
              <button onClick={() => navigate("/register")}>Register</button>
              <button onClick={() => navigate("/login")}>Login</button>
            </div>
          </>
        )}
      </div>
      <div className="shops-list">
        <h1>Shops</h1>
        {shopList.map((shop) => (
          <ShopCard key={shop.id} shop={shop} />
        ))}
      </div>
    </div>
  );
}
