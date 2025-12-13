import React from "react";
import { useLocation } from "react-router-dom";
import "../css/ManageBookings.css";
import { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/Context";
import { useNavigate } from "react-router-dom";

export default function ManageBookings() {
  const location = useLocation();
  const shop = location.state?.shop;
  const [appointments, setAppointments] = useState([]);
  const { token } = useAuth();
  const [appointmentType, setAppointmentType] = useState("upcoming");
  const [filterDate, setFilterDate] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchAppointments() {
      try {
        const res = await axios.get(
          `http://localhost:8080/api/appointments/confirmed/${shop.id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              type: `${appointmentType}`,
              date: filterDate,
            },
          }
        );
        setAppointments(res.data);
        console.log("RES: ", res);
      } catch (error) {
        console.log("ERROR: ", error);
      }
    }
    if (shop?.id) fetchAppointments();
  }, [appointmentType, filterDate]);

  function handleTypeChange() {
    if (appointmentType === "upcoming") {
      setAppointmentType("past");
    }

    if (appointmentType === "past") {
      setAppointmentType("upcoming");
    }
  }

  return (
    <div className="manage-bookings-container">
      <button className="back-button" onClick={() => navigate(-1)}>
        {"<"}
      </button>
      <div class className="manage-bookings-filter">
        {appointmentType === "upcming" ? (
          <button onClick={handleTypeChange}>View upcoming appointments</button>
        ) : (
          <button onClick={handleTypeChange}>View past appointments</button>
        )}
        <div className="manage-bookings-filter-by-date">
          <strong>Filter by date:</strong>
          <input
            type="date"
            value={filterDate || ""}
            onChange={(e) => setFilterDate(e.target.value)}
          />
        </div>
        <button onClick={() => setFilterDate(null)}>Reset</button>
      </div>

      <div className="appointments-area">
        {appointments.length > 0 ? (
          appointments.map((apt) => (
            <div className="appointment-card">
              <h3>{apt.service.name}</h3>
              <p>
                <strong>Customer: </strong>
                {apt.user.name}
              </p>
              <p>
                <strong>Customer e-mail: </strong>
                {apt.user.email}
              </p>
              <p>
                <strong>Price: </strong>${apt.service.price}
              </p>
              <p>
                <strong>Date: </strong>
                {apt.appointmentDate}
              </p>
              <p>
                <strong>Time: </strong>
                {apt.appointmentTime}
              </p>
            </div>
          ))
        ) : (
          <h1>No appointments</h1>
        )}
      </div>
    </div>
  );
}
