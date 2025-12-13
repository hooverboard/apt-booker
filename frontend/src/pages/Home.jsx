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
  const [appointment, setAppointments] = useState([]);

  useEffect(() => {
    async function fetchShops() {
      const res = await axios.get("http://localhost:8080/api/shops");

      console.log(res);
      setShopList(res.data);
    }

    fetchShops();
  }, []);

  useEffect(() => {
    async function fetchAppointments() {
      try {
        const res = await axios.get(
          "http://localhost:8080/api/appointments/my-appointments",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setAppointments(res.data);
      } catch (error) {
        console.log("Failed to fetch appointments", error);
      }
    }
    if (token) fetchAppointments();
  }, [token]);

  return (
    <div className="home-container">
      <div className="user-info">
        {user ? (
          <>
            <h2>Welcome, {user.name}!</h2>
            <div className="user-info-buttons">
              {user.role === "host" && (
                <div>
                  <button onClick={() => navigate("/shops/manage")}>
                    Manage shops
                  </button>
                </div>
              )}
              <button onClick={logout}>Logout</button>
            </div>
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
      <div className="my-appointments-area">
        <h2>Your appointments</h2>
        <div className="home-appointment-card-container">
          {appointment.map((appointment) => {
            const shop = shopList.find((s) => s.id === appointment.shopId);

            const service = shop?.services?.find(
              (srv) => srv.id === appointment.serviceId
            );
            return (
              <div key={appointment.id} className="home-appointment-card">
                <p>Shop: {shop ? shop.name : `ID ${appointment.shopId}`}</p>
                <p>
                  Service:{" "}
                  {service ? service.name : `ID ${appointment.serviceId}`}
                </p>
                <p>Date: {appointment.appointmentDate}</p>
                <p>Time: {appointment.appointmentTime}</p>
              </div>
            );
          })}
        </div>
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
