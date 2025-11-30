import React from "react";
import { useState } from "react";
import axios from "axios";

export default function Register() {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    role: "user",
  });

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  // Submit registration data to backend
  //enviar dados de registro para o backend
  async function handleSubmit(e) {
    e.preventDefault();

    try {
      console.log("attempting to register with data:", form);
      let res = await axios.post(
        "http://localhost:8080/api/auth/register",
        form
      );
      console.log(res);
    } catch (error) {
      console.error("Registration error:", error);
      if (error.response) {
        console.error("Response data:", error.response.data);
      }
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <h1>Register</h1>
      <input
        type="text"
        placeholder="Name"
        name="name"
        onChange={handleChange}
      />
      <input
        type="email"
        placeholder="Email"
        name="email"
        onChange={handleChange}
      />
      <input
        type="password"
        placeholder="Password"
        name="password"
        onChange={handleChange}
      />
      <input
        type="password"
        placeholder="Confirm Password"
        name="confirmPassword"
        onChange={handleChange}
      />
      <select name="role" value={form.role} onChange={handleChange}>
        <option value="user">User</option>
        <option value="host">Host</option>
      </select>
      <button type="submit">Register</button>
    </form>
  );
}
