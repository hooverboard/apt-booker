import React from "react";
import { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import EditService from "../components/EditService";
import axios from "axios";
import { useAuth } from "../context/Context";
import toast from "react-hot-toast";

export default function EditShopServices() {
  const location = useLocation();
  const { shop } = location.state || {};
  const [services, setServices] = useState(shop?.services || []);
  const { token } = useAuth();
  console.log("SHOP :", shop);

  async function handleServiceUpdate(updatedData) {
    setServices((prevServices) =>
      prevServices.map((service) => {
        if (service.id === updatedData.id) {
          console.log("Updating service:", service.id);

          // send to backend for update
          console.log("Attempting to update service");
          const res = axios.put(
            "http://localhost:8080/api/services",
            updatedData,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
          toast.success("Service updated");
          console.log("Service updated successfully: ", res);

          return { ...service, ...updatedData };
        } else {
          return service;
          toast.error("Failed to update");
        }
      })
    );
  }

  useEffect(() => {
    console.log("Services state updated:", services);
  }, [services]);

  return (
    <div>
      {services.map((service) => (
        <EditService
          id={service.id}
          name={service.name}
          description={service.description}
          duration={service.duration}
          price={service.price}
          shopId={shop.id}
          onUpdate={handleServiceUpdate}
        />
      ))}
    </div>
  );
}
