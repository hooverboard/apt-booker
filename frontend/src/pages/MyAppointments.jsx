import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/Context";
import "../css/MyAppointments.css"; // We'll create this CSS file

export default function MyAppointments() {
  const [appointments, setAppointments] = useState([]);
  const { token } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchMyAppointments() {
      if (!token) {
        setLoading(false);
        setError("User not authenticated.");
        return;
      }

      try {
        setLoading(true);
        const res = await axios.get(
          "http://localhost:8080/api/appointments/my-appointments",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setAppointments(res.data);
      } catch (err) {
        console.error("Error fetching my appointments:", err);
        setError("Failed to fetch appointments.");
      } finally {
        setLoading(false);
      }
    }

    fetchMyAppointments();
  }, [token]);

  if (loading) {
    return <div className="my-appointments-container">Loading appointments...</div>;
  }

  if (error) {
    return <div className="my-appointments-container error-message">{error}</div>;
  }

  return (
    <div className="my-appointments-container">
      <h1>My Appointments</h1>
      <div className="appointments-list">
        {appointments.length > 0 ? (
          appointments.map((appt) => (
            <div key={appt.id} className="appointment-card">
              <h3>Appointment ID: {appt.id}</h3>
              <p>Date: {appt.appointmentDate}</p>
              <p>Time: {appt.appointmentTime}</p>
              <p>Status: {appt.status}</p>
              {/* Add more details as needed, like service name or shop name if available */}
              {/* For now, we only have serviceId and shopId */}
              <p>Service ID: {appt.serviceId}</p>
              <p>Shop ID: {appt.shopId}</p>
            </div>
          ))
        ) : (
          <p>You have no appointments.</p>
        )}
      </div>
    </div>
  );
}
