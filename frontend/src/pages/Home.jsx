import React from "react";
import "../css/Home.css";
import { useAuth } from "../context/Context";

export default function Home() {
  const { user, token, logout } = useAuth();

  return (
    <div>
      <h1>Home Page</h1>
      {user ? (
        <div>
          <h2>Welcome, {user.name}!</h2>
          <p>Email: {user.email}</p>
          <p>Role: {user.role}</p>
          <p>User ID: {user.id}</p>
          <button onClick={logout}>Logout</button>
        </div>
      ) : (
        <p>Please login to view shops</p>
      )}
    </div>
  );
}
