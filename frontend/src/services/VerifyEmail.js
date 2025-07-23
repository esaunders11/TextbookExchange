import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";

function VerifyEmail() {
  const [message, setMessage] = useState("Verifying...");
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token");

    if (token) {
      fetch(`/api/auth/verify?token=${token}`)
        .then((res) => {
          if (!res.ok) {
            throw new Error(res.text());
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