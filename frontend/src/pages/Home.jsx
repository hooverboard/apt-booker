import React from "react";
import "../css/Home.css";
import { useAuth } from "../context/Context";
import ShopCard from "../components/ShopCard";

export default function Home() {
  const { user, token, logout } = useAuth();

  // Example shop data
  const shops = [
    {
      id: 1,
      name: "Classic Cuts Barbershop",
      type: "Barbershop",
      city: "New York",
      description:
        "Professional barbershop specializing in classic and modern haircuts",
      hours: "Mon-Sat: 9:00 AM - 7:00 PM",
      image:
        "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=400&h=400&fit=crop",
    },
    {
      id: 2,
      name: "Serenity Spa & Wellness",
      type: "Spa",
      city: "Los Angeles",
      description:
        "Full-service spa offering massages, facials, and wellness treatments",
      hours: "Mon-Sun: 10:00 AM - 8:00 PM",
      image:
        "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=400&h=400&fit=crop",
    },
    {
      id: 3,
      name: "Glamour Hair Salon",
      type: "Hair Salon",
      city: "Chicago",
      description:
        "Expert stylists for cuts, color, and special occasion styling",
      hours: "Tue-Sun: 9:00 AM - 6:00 PM",
      image:
        "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=400&h=400&fit=crop",
    },
  ];

  return (
    <div className="home-container">
      <h1>Appointment Booker</h1>
      {user ? (
        <div>
          <div className="user-info">
            <h2>Welcome, {user.name}!</h2>
            <button onClick={logout}>Logout</button>
          </div>
          <div className="shops-list">
            {shops.map((shop) => (
              <ShopCard key={shop.id} shop={shop} />
            ))}
          </div>
        </div>
      ) : (
        <p>Please login to view shops</p>
      )}
    </div>
  );
}
