import React from "react";
import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import EditService from "../components/EditService";
import axios from "axios";
import { useAuth } from "../context/Context";
import toast from "react-hot-toast";
import "../css/EditShopServicesPage.css";

export default function EditShopServices() {
  const location = useLocation();
  const { shop } = location.state || {};
  const [services, setServices] = useState(shop?.services || []);
  const { token } = useAuth();
  const [blankService, setBlankService] = useState({
    name: "Service",
    description: "Service Description",
    duration: 30,
    price: 50,
    shopId: shop.id,
  });

  const navigate = useNavigate();

  async function handleServiceUpdate(updatedData) {
    setServices((prevServices) =>
      prevServices.map((service) =>
        service.id === updatedData.id ? { ...service, ...updatedData } : service
      )
    );
    try {
      await axios.put("http://localhost:8080/api/services", updatedData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Service updated");
    } catch (error) {
      toast.error("Failed to update");
    }
  }

  async function fetchServices() {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/shops/${shop.id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setServices(res.data.services);
    } catch (error) {
      toast.error("Failed to fetch services");
    }
  }

  async function handleServiceDelete(serviceId) {
    try {
      await axios.delete(`http://localhost:8080/api/services/${serviceId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Service deleted");
      await fetchServices();
    } catch (error) {
      toast.error("Failed to delete service");
    }
  }

  useEffect(() => {
    fetchServices();
  }, []);

  async function createNewService() {
    console.log("Attempting to create service...");
    try {
      await axios.post("http://localhost:8080/api/services", blankService, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Service created");
      await fetchServices();
    } catch (error) {
      console.log("ERROR: ", error);
      toast.error("Failed to create service");
    }
  }

  return (
    <div>
      <button className="back-button" onClick={() => navigate(-1)}>
        {"<"}
      </button>
      <button className="create-shop-button" onClick={createNewService}>
        +
      </button>
      {services.map((service) => (
        <EditService
          id={service.id}
          name={service.name}
          description={service.description}
          duration={service.duration}
          price={service.price}
          shopId={shop.id}
          onUpdate={handleServiceUpdate}
          onDelete={() => handleServiceDelete(service.id)}
        />
      ))}
    </div>
  );
}
