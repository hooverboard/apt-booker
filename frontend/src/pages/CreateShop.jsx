import React from "react";
import "../css/CreateShop.css";
import { useState } from "react";
import axios from "axios";
import { useAuth } from "../context/Context";
import { toast } from "react-hot-toast";

export default function CreateShop() {
  const { token } = useAuth();

  const [form, setForm] = useState({
    name: "",
    address: "",
    description: "",
    phoneNumber: "",
    imageUrl: "",
    openingTime: "",
    closingTime: "",
  });

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    console.log(form);

    if (
      !form.name ||
      !form.address ||
      !form.description ||
      !form.phoneNumber ||
      !form.imageUrl ||
      !form.openingTime ||
      !form.closingTime
    ) {
      toast.error("All fields are required");
    }
    try {
      const res = await axios.post("http://localhost:8080/api/shops", form, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log("Success:", res.data);
      toast.success("Shop created");
    } catch (error) {
      console.error("Full error:", error.response?.data);
      toast.error("Shop creation failed");
    }
  }

  return (
    <div>
      <h1>Create your shop</h1>
      <form onSubmit={handleSubmit} className="form">
        <input
          type="text"
          placeholder="Shop name"
          name="name"
          onChange={handleChange}
        />
        <input
          type="text"
          placeholder="Shop address"
          name="address"
          onChange={handleChange}
        />
        <input
          type="text"
          placeholder="Phone number"
          name="phoneNumber"
          onChange={handleChange}
        />
        <input
          type="text"
          placeholder="Description"
          name="description"
          onChange={handleChange}
        />
        <div>
          <input
            type="time"
            placeholder="Opening time"
            className="storeHours"
            name="openingTime"
            onChange={handleChange}
          />
          <input
            type="time"
            placeholder="Closing time"
            className="storeHours"
            name="closingTime"
            onChange={handleChange}
          />
        </div>
        <input
          type="text"
          placeholder="Shop image URL"
          name="imageUrl"
          onChange={handleChange}
        />
        <button type="submit">Create</button>
      </form>
    </div>
  );
}
