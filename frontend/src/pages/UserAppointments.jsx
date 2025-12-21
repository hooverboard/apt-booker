import React from "react";
import "../css/UserAppointments.css";
import { useState, useEffect } from "react";
import api from "../api/axiosConfig";
import { useAuth } from "../context/Context";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

const UserAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const { token } = useAuth();
  const [appointmentType, setAppointmentType] = useState("upcoming");
  const navigate = useNavigate();

  useEffect(() => {
    async function getAppointments() {
      try {
        console.log("attempting to fetch appointments");
        const res = await api.get("/api/appointments/my-appointments", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: {
            type: `${appointmentType}`,
          },
        });
        console.log("appointments fetched");
        setAppointments([]);
        setAppointments(res.data);
        console.log("RESPONSE: ", res);
      } catch (error) {
        console.log("ERROR: ", error);
      }
    }

    getAppointments();
  }, [appointmentType]);

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
      const res = await api.delete(`/api/appointments/${aptId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Appointment deleted");
      console.log("RES: ", res);
      window.location.reload();
    } catch (error) {
      console.log("ERROR: ", error);
      toast.error("Failed to delete");
    }
  }

  return (
    <div className="user-appointments-container">
      <button className="back-button" onClick={() => navigate(-1)}>
        {"<"}
      </button>
      <h1>Your appointments</h1>
      {appointmentType === "upcoming" ? (
        <button onClick={handleTypeChange}>View past appointments</button>
      ) : (
        <button onClick={handleTypeChange}>View upcoming appointments</button>
      )}
      <div className="user-appointments-card-area">
        {appointments.length > 0 ? (
          appointments.map((appointment) => (
            <div className="user-appointments-card" key={appointment?.id}>
              <h3>{appointment?.service?.name || "-"}</h3>
              <strong>Shop</strong>
              <p>{appointment?.shop?.name || "-"}</p>
              <strong>Date</strong>
              <p>{appointment?.appointmentDate || "-"}</p>
              <strong>Time</strong>
              <p>{appointment?.appointmentTime?.slice(0, 5) || "-"}</p>
              <strong>Address</strong>
              <p>{appointment?.shop?.address || "-"}</p>
              <strong>Phone number</strong>
              <p>{appointment?.shop?.phoneNumber || "-"}</p>
              <button onClick={() => handleDelete(appointment?.id)}>
                Delete
              </button>
            </div>
          ))
        ) : (
          <h1>not found</h1>
        )}
      </div>
    </div>
  );
};

export default UserAppointments;
