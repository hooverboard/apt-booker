import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import "../css/ShopPage.css";

export default function ShopPage() {
  const location = useLocation();
  const { shop } = location.state || {};

  const [selectedService, setSelectedService] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);

  if (!shop) {
    return <div className="shop-page-container">Shop information not found.</div>;
  }

  const services = [
    "Hair Cut",
    "Beard Trim",
    "Hair Cut & Beard Trim",
  ];

  const availableTimes = [
    "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
    "11:00 AM", "11:30 AM", "1:00 PM", "1:30 PM",
    "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM",
    "4:00 PM", "4:30 PM", "5:00 PM"
  ];

  return (
    <div className="shop-page-container">
      <div className="shop-page-header">
        <img src={shop.image} alt={shop.name} className="shop-page-image" />
        <div className="shop-page-details">
          <h1>{shop.name}</h1>
          <p className="shop-page-type">{shop.type}</p>
          <p className="shop-page-location">📍 {shop.city}</p>
          <p className="shop-page-description">{shop.description}</p>
          <p className="shop-page-hours">⏰ {shop.hours}</p>
        </div>
      </div>

      <div className="shop-page-selection-section">
        <h2>Select a Service</h2>
        <div className="service-options">
          {services.map((service) => (
            <button
              key={service}
              className={`service-button ${selectedService === service ? "selected" : ""}`}
              onClick={() => setSelectedService(service)}
            >
              {service}
            </button>
          ))}
        </div>

        {selectedService && (
          <>
            <h2>Select a Time for {selectedService}</h2>
            <div className="time-options">
              {availableTimes.map((time) => (
                <button
                  key={time}
                  className={`time-button ${selectedTime === time ? "selected" : ""}`}
                  onClick={() => setSelectedTime(time)}
                >
                  {time}
                </button>
              ))}
            </div>
          </>
        )}

        {selectedService && selectedTime && (
          <div className="booking-summary">
            <h3>Booking Summary:</h3>
            <p>Shop: {shop.name}</p>
            <p>Service: {selectedService}</p>
            <p>Time: {selectedTime}</p>
            <button className="confirm-booking-btn">Confirm Booking</button>
          </div>
        )}
      </div>
    </div>
  );
}
