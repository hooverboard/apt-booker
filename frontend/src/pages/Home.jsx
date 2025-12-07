import React from "react";
import "../css/Home.css";
import { useAuth } from "../context/Context";
import ShopCard from "../components/ShopCard";
import { useState, useEffect } from "react";
import axios from "axios";

export default function Home() {
  const { user, token, logout } = useAuth();
  const [shopList, setShopList] = useState([]);

  useEffect(() => {
    async function fetchShops() {
      const res = await axios.get("http://localhost:8080/api/shops");
      console.log(res);
      setShopList(res.data);
      setTimeout(3000);
      console.log(shopList);
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
              <button onClick={logout}>Logout</button>
            </>
          ) : (
            <h2>Browse Available Services</h2>
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
