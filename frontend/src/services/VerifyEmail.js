import React, { useEffect, useState, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";

function VerifyEmail() {
  const [message, setMessage] = useState("Verifying...");
  const navigate = useNavigate();
  const location = useLocation();
  const hasFetched = useRef(false);

  const API_BASE_URL = process.env.REACT_APP_API_URL;

  useEffect(() => {
    if (hasFetched.current) return;
    hasFetched.current = true;
    const params = new URLSearchParams(location.search);
    const token = params.get("token");

    if (token) {
      fetch(`${API_BASE_URL}/api/auth/verify?token=${token}`)
        .then((res) => {
          if (!res.ok) {
            throw new Error("Invalid or expired token");
          }
          return res.text();
        })
        .then(() => {
          setMessage("Email verified! Redirecting to login...");
          setTimeout(() => {
            navigate("/login", { state: { fromVerify: true } });
          }, 2000);
        })
        .catch((err) => {
          setMessage("Verification failed: " + err.message);
        });
    } else {
      setMessage("No token provided.");
    }
  }, [location.search, navigate]);

  return (
    <div>
      <h2>Email Verification</h2>
      <p>{message}</p>
    </div>
  );
}

export default VerifyEmail;