import React from "react";
import { useState, useEffect } from "react";
import "../css/EditServiceComponent.css";

export default function EditService({
  id,
  name,
  description,
  duration,
  price,
  onUpdate,
  shopId,
}) {
  const [serviceData, setServiceData] = useState({
    id,
    name,
    description,
    duration,
    price,
    shopId,
  });

  function hadleUpdate() {
    onUpdate(serviceData);
  }

  function handleChange(e) {
    setServiceData((prevData) => ({
      ...prevData,
      [e.target.name]: e.target.value,
    }));
  }

  return (
    <div className="edit-service-container">
      <div className="edit-service-section">
        <p>Name</p>
        <input
          type="text"
          value={serviceData.name}
          name="name"
          onChange={handleChange}
        />
        <p>Description</p>
        <textarea
          value={serviceData.description}
          name="description"
          onChange={handleChange}
          className="edit-service-description"
        />
        <div className="price-duration-area">
          <div>
            <p>Duration (minutes)</p>
            <input
              type="number"
              value={serviceData.duration}
              name="duration"
              onChange={handleChange}
            />
          </div>
          <div>
            <p>Price</p>
            <input
              type="number"
              value={serviceData.price}
              name="price"
              onChange={handleChange}
            />
          </div>
        </div>
        <button onClick={hadleUpdate}>Save</button>
      </div>
    </div>
  );
}
