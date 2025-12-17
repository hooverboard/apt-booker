import React from "react";
import { useLocation } from "react-router-dom";
import "../css/ManageBookings.css";
import { useState, useEffect } from "react";
import api from "../api/axiosConfig";
import { useAuth } from "../context/Context";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

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
        const res = await api.get(
          `/api/appointments/confirmed/${shop.id}`,
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

  async function handleDelete(aptId) {
    try {
      const res = await api.delete(
        `/api/appointments/${aptId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      toast.success("Appointment deleted");
      console.log("RES: ", res);
      window.location.reload();
    } catch (error) {
      console.log("ERROR: ", error);
      toast.error("Failed to delete");
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
              <button onClick={() => handleDelete(apt.id)}>Delete</button>
            </div>
          ))
        ) : (
          <h1>No appointments</h1>
        )}
      </div>
    </div>
  );
}
