import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import toast from "react-hot-toast";
import { useAuth } from "../context/Context";
import "../css/ManageShops.css";

export default function ManageShops() {
  const { token, user } = useAuth();
  const navigate = useNavigate();
  const [shops, setShops] = useState([]);

  useEffect(() => {
    const fetchMyShops = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/shops/my-shops",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setShops(response.data);
      } catch (error) {
        console.error("Error fetching shops:", error);
        toast.error("Failed to load your shops");
      }
    };

    if (token && user?.role === "host") {
      fetchMyShops();
    }
  }, [token, user]);

  if (!user || user.role !== "host") {
    return (
      <div className="manage-shops-container">
        <p>You must be a host to manage shops.</p>
      </div>
    );
  }

  return (
    <div className="manage-shops-container">
      <button className="back-button" onClick={() => navigate(-1)}>
        {"<"}
      </button>
      <div className="manage-shops-header">
        <h1>Manage Your Shops</h1>
        <button
          className="create-shop-btn"
          onClick={() => navigate("/shops/create")}
        >
          Create New Shop
        </button>
      </div>

      {shops.length === 0 ? (
        <div className="no-shops">
          <p>You don't have any shops yet.</p>
          <button
            className="create-first-shop-btn"
            onClick={() => navigate("/shops/create")}
          >
            Create Your First Shop
          </button>
        </div>
      ) : (
        <div className="shops-grid">
          {shops.map((shop) => (
            <div key={shop.id} className="manage-shop-card">
              <img
                src={shop.imageUrl}
                alt={shop.name}
                className="manage-shop-image"
              />
              <div className="manage-shop-details">
                <h2>{shop.name}</h2>
                <p className="shop-address">📍 {shop.address}</p>
                <p className="shop-description">{shop.description}</p>
                <p className="shop-phone">
                  ☎️ ({shop.phoneNumber.slice(0, 3)}){" "}
                  {shop.phoneNumber.slice(3, 6)}-{shop.phoneNumber.slice(6, 10)}
                </p>
                <p className="shop-hours">
                  ⏰ {shop.openingTime.slice(0, 5)} -{" "}
                  {shop.closingTime.slice(0, 5)}
                </p>
                <button
                  onClick={() =>
                    navigate("/bookings/manage", { state: { shop } })
                  }
                >
                  Manage appointments
                </button>
                <div className="shop-services">
                  <div>
                    <strong>Services:</strong>
                    <ul>
                      {shop.services.map((service) => (
                        <li key={service.id}>
                          {service.name} - ${service.price} ({service.duration}{" "}
                          min)
                        </li>
                      ))}
                    </ul>
                  </div>
                  <button
                    className="edit-service-btn"
                    onClick={() => {
                      navigate(`/shops/${shop.id}/services/edit`, {
                        state: { shop },
                      });
                    }}
                  >
                    Edit services
                  </button>
                </div>
                <div className="manage-shop-actions">
                  <button
                    className="edit-shop-btn"
                    onClick={() =>
                      navigate(`/shops/${shop.id}/edit`, {
                        state: { shop },
                      })
                    }
                  >
                    Edit Shop
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
