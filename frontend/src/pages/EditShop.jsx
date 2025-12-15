import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import toast from "react-hot-toast";
import { useAuth } from "../context/Context";
import "../css/EditShop.css";

export default function EditShop() {
  const location = useLocation();
  const navigate = useNavigate();
  const { token } = useAuth();
  const { shop } = location.state || {};

  const [formData, setFormData] = useState({
    name: shop?.name || "",
    address: shop?.address || "",
    description: shop?.description || "",
    phoneNumber: shop?.phoneNumber || "",
    imageUrl: shop?.imageUrl || "",
    openingTime: shop?.openingTime?.slice(0, 5) || "",
    closingTime: shop?.closingTime?.slice(0, 5) || "",
  });

  if (!shop) {
    return (
      <div className="edit-shop-container">
        <p>Shop information not found.</p>
        <button onClick={() => navigate("/shops/manage")}>
          Back to Manage Shops
        </button>
      </div>
    );
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handlePhoneChange = (e) => {
    const value = e.target.value.replace(/\D/g, "");
    setFormData({
      ...formData,
      phoneNumber: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.name || !formData.address || !formData.phoneNumber) {
      toast.error("Please fill in all required fields");
      return;
    }

    if (formData.phoneNumber.length !== 10) {
      toast.error("Phone number must be 10 digits");
      return;
    }

    try {
      const updateData = {
        name: formData.name,
        address: formData.address,
        description: formData.description,
        phoneNumber: formData.phoneNumber,
        imageUrl: formData.imageUrl,
        openingTime: formData.openingTime + ":00",
        closingTime: formData.closingTime + ":00",
      };

      await axios.put(
        `http://localhost:8080/api/shops/${shop.id}`,
        updateData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      toast.success("Shop updated successfully!");
      navigate("/shops/manage");
    } catch (error) {
      console.error("Error updating shop:", error);
      const errorMessage =
        error.response?.data?.errorMessage || "Failed to update shop";
      toast.error(errorMessage);
    }
  };

  async function handleDelete() {
    const ok = window.confirm("Are you sure you want to delete this shop?");

    if (ok) {
      try {
        const res = axios.delete(`http://localhost:8080/api/shops/${shop.id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        console.log("RES: ", res);
        toast.success("Shop deleted");
      } catch (error) {
        console.log("ERROR: ", error);
        toast.error("Failed to delete");
      }
    } else {
      return;
    }
  }

  return (
    <div className="edit-shop-container">
      <button className="back-button" onClick={() => navigate(-1)}>
        {"<"}
      </button>
      <h1>Edit Shop</h1>
      <form onSubmit={handleSubmit} className="edit-shop-form">
        <div className="form-group">
          <label htmlFor="name">Shop Name *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="address">Address *</label>
          <input
            type="text"
            id="address"
            name="address"
            value={formData.address}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="4"
          />
        </div>

        <div className="form-group">
          <label htmlFor="phoneNumber">Phone Number * (10 digits)</label>
          <input
            type="tel"
            id="phoneNumber"
            name="phoneNumber"
            value={formData.phoneNumber}
            onChange={handlePhoneChange}
            placeholder="1234567890"
            maxLength="10"
            required
          />
          {formData.phoneNumber && (
            <small className="phone-preview">
              Preview: ({formData.phoneNumber.slice(0, 3)}){" "}
              {formData.phoneNumber.slice(3, 6)}-
              {formData.phoneNumber.slice(6, 10)}
            </small>
          )}
        </div>

        <div className="form-group">
          <label htmlFor="imageUrl">Image URL</label>
          <input
            type="url"
            id="imageUrl"
            name="imageUrl"
            value={formData.imageUrl}
            onChange={handleChange}
            placeholder="https://example.com/image.jpg"
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="openingTime">Opening Time *</label>
            <input
              type="time"
              id="openingTime"
              name="openingTime"
              value={formData.openingTime}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="closingTime">Closing Time *</label>
            <input
              type="time"
              id="closingTime"
              name="closingTime"
              value={formData.closingTime}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-actions">
          <button type="button" onClick={handleDelete}>
            Delete
          </button>
          <button type="submit" className="save-btn">
            Save Changes
          </button>
        </div>
      </form>
    </div>
  );
}
