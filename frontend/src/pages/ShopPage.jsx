import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import toast from "react-hot-toast";
import { useAuth } from "../context/Context";
import "../css/ShopPage.css";

export default function ShopPage() {
  const location = useLocation();
  const { shop } = location.state || {};
  const { token, user } = useAuth();

  const [selectedService, setSelectedService] = useState(null);
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedTime, setSelectedTime] = useState(null);
  const [availableTimes, setAvailableTimes] = useState([]);
  const [loading, setLoading] = useState(false);

  if (!shop) {
    return (
      <div className="shop-page-container">Shop information not found.</div>
    );
  }

  // Fetch available times when service and date are selected
  useEffect(() => {
    const fetchAvailableTimes = async () => {
      if (!selectedService || !selectedDate) {
        setAvailableTimes([]);
        return;
      }

      setLoading(true);
      try {
        const response = await axios.get(
          "http://localhost:8080/api/appointments/available-times",
          {
            params: {
              shopId: shop.id,
              serviceId: selectedService.id,
              date: selectedDate,
            },
          }
        );

        // Convert LocalTime format (HH:mm:ss) to display format
        const times = response.data.availableTimes.map((time) => {
          const [hours, minutes] = time.split(":");
          const hour = parseInt(hours);
          const ampm = hour >= 12 ? "PM" : "AM";
          const displayHour = hour % 12 || 12;
          return `${displayHour}:${minutes} ${ampm}`;
        });

        setAvailableTimes(times);
        setSelectedTime(null); // Reset selected time when available times change
      } catch (error) {
        console.error("Error fetching available times:", error);
        toast.error("Failed to load available times");
        setAvailableTimes([]);
      } finally {
        setLoading(false);
      }
    };

    fetchAvailableTimes();
  }, [selectedService, selectedDate, shop.id]);

  // Convert display time back to LocalTime format for backend
  const convertToLocalTime = (displayTime) => {
    const [time, period] = displayTime.split(" ");
    const [hours, minutes] = time.split(":");
    let hour = parseInt(hours);

    if (period === "PM" && hour !== 12) hour += 12;
    if (period === "AM" && hour === 12) hour = 0;

    return `${hour.toString().padStart(2, "0")}:${minutes}:00`;
  };

  const handleConfirmBooking = async () => {
    if (!selectedService || !selectedDate || !selectedTime) {
      toast.error("Please select a service, date, and time");
      return;
    }

    if (!user) {
      toast.error("Please login to book an appointment");
      return;
    }

    try {
      const appointmentData = {
        shopId: shop.id,
        serviceId: selectedService.id,
        appointmentDate: selectedDate,
        appointmentTime: convertToLocalTime(selectedTime),
      };

      await axios.post(
        "http://localhost:8080/api/appointments",
        appointmentData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      toast.success("Appointment booked successfully!");
      // Reset selections
      setSelectedService(null);
      setSelectedDate("");
      setSelectedTime(null);
    } catch (error) {
      console.error("Error booking appointment:", error);
      const errorMessage =
        error.response?.data?.errorMessage || "Failed to book appointment";
      toast.error(errorMessage);
    }
  };

  // Get today's date in YYYY-MM-DD format for min attribute
  const today = new Date().toISOString().split("T")[0];

  return (
    <div className="shop-page-container">
      <div className="shop-page-header">
        <img src={shop.imageUrl} alt={shop.name} className="shop-page-image" />
        <div className="shop-page-details">
          <h1>{shop.name}</h1>
          <p className="shop-page-type">{shop.type}</p>
          <p className="shop-page-location">📍 {shop.address}</p>
          <p className="shop-page-description">{shop.description}</p>
          <p>
            ☎️ ({shop.phoneNumber.slice(0, 3)}) {shop.phoneNumber.slice(3, 6)} -
            {shop.phoneNumber.slice(6, 10)}
          </p>
          <p className="shop-page-hours">
            ⏰ {shop.openingTime.slice(0, 5)} - {shop.closingTime.slice(0, 5)}
          </p>
        </div>
      </div>

      <div className="shop-page-selection-section">
        <h2>Select a Service</h2>
        <div className="service-options">
          {shop.services && shop.services.length > 0 ? (
            shop.services.map((service) => (
              <button
                key={service.id}
                className={`service-button ${
                  selectedService?.id === service.id ? "selected" : ""
                }`}
                onClick={() => setSelectedService(service)}
              >
                <div className="service-info">
                  <h3>{service.name}</h3>
                  <p>{service.description}</p>
                  <p className="service-price">${service.price}</p>
                  <p className="service-duration">{service.duration} min</p>
                </div>
              </button>
            ))
          ) : (
            <p>No services available</p>
          )}
        </div>

        <h2>Select a Date</h2>
        <div className="date-picker-container">
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => {
              setSelectedDate(e.target.value);
              setSelectedTime(null); // Reset time when date changes
            }}
            min={today}
            className="date-picker"
          />
        </div>

        <h2>Select a Time</h2>
        <div className="time-options">
          {loading ? (
            <p>Loading available times...</p>
          ) : !selectedService || !selectedDate ? (
            <p>Please select a service and date first</p>
          ) : availableTimes.length === 0 ? (
            <p>No available times for this date</p>
          ) : (
            availableTimes.map((time) => (
              <button
                key={time}
                className={`time-button ${
                  selectedTime === time ? "selected" : ""
                }`}
                onClick={() => setSelectedTime(time)}
              >
                {time}
              </button>
            ))
          )}
        </div>

        <div className="booking-summary">
          <h3>Booking Summary:</h3>
          {selectedService && selectedDate && selectedTime ? (
            <>
              <p>Shop: {shop.name}</p>
              <p>Service: {selectedService.name}</p>
              <p>Price: ${selectedService.price}</p>
              <p>Duration: {selectedService.duration} minutes</p>
              <p>
                Date:{" "}
                {new Date(selectedDate + "T00:00:00").toLocaleDateString()}
              </p>
              <p>Time: {selectedTime}</p>
              <button
                className="confirm-booking-btn"
                onClick={handleConfirmBooking}
              >
                Confirm Booking
              </button>
            </>
          ) : (
            <p>Please select a service, date, and time to book</p>
          )}
        </div>
      </div>
    </div>
  );
}
