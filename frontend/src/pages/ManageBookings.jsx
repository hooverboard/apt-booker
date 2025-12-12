import React from "react";
import { useLocation } from "react-router-dom";
import "../css/ManageBookings.css";
import { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/Context";

export default function ManageBookings() {
  const location = useLocation();
  const shop = location.state?.shop;
  const [appointments, setAppointments] = useState([]);
  const { token } = useAuth();

  useEffect(() => {
    async function fetchAppointments() {
      try {
        const res = await axios.get(
          `http://localhost:8080/api/appointments/confirmed/${shop.id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
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
  }, [shop, token]);

  useEffect(() => {
    console.log(appointments);
  }, [appointments]);

  return (
    <div className="manage-bookings-container">
      <h1>Upcoming Bookings</h1>
      {shop && (
        <div className="shop-info">
          <h2>For Shop: {shop.name}</h2>
        </div>
      )}
      <div className="appointments-area">
        {appointments.length > 0 ? (
          appointments.map((appt) => {
            // Find the service object in shop.services that matches the appointment's serviceId
            const service = shop?.services?.find(
              (service) => service.id === appt.serviceId
            );
            return (
              <div key={appt.id} className="appointment-card">
                <div>
                  <h3>Date</h3>
                  <p>{appt.appointmentDate}</p>
                </div>
                <div>
                  <h3>Time</h3>
                  <p>{appt.appointmentTime}</p>
                </div>
                <div>
                  <h3>Service</h3>
                  <p>{service ? service.name : `ID ${appt.serviceId}`}</p>
                </div>
              </div>
            );
          })
        ) : (
          <p>No appointments found.</p>
        )}
      </div>
    </div>
  );
}
