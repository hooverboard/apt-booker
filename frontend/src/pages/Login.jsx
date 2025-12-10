import React from "react";
import { useState } from "react";
import axios from "axios";
import { toast } from "react-hot-toast";
import { useAuth } from "../context/Context";
import { useNavigate, Link } from "react-router-dom";
import "../css/Login.css";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();

    // Submit login data to backend
    //enviar dados de login para o backend
    try {
      console.log("attempting to login with data:", form);
      let res = await axios.post("http://localhost:8080/api/auth/login", form);
      console.log("User logged in successfully", res.data);
      toast.success("Login successful");

      // save token and user data
      // salvar token e dados do usuario
      const { token, ...userData } = res.data;
      login(userData, token);
      navigate("/");
    } catch (error) {
      console.log("Login error:", error);
      if (error.response) {
        console.log("Response data:", error.response.data);
        toast.error(error.response.data.errorMessage);
      }
    }
  }

  return (
    <div className="container">
      <form onSubmit={handleSubmit}>
        <h1>Login</h1>
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
        <button type="submit">Submit</button>
        <p className="auth-link">
          Not registered? <Link to="/register">Sign up</Link>
        </p>
      </form>
    </div>
  );
}
