import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { loginWithSaml2, checkSession } = useAuth();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
        credentials: 'include'
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      await checkSession(); // Update auth context
      window.location.href = '/';
    } catch (err) {
      setError("Login Failed.");
    } finally {
      setLoading(false);
    }
  };

  const handleSaml2Login = () => {
    loginWithSaml2();
  };

  return (
    <div className="form-container">
      <h2>Login to Your Account</h2>
      <p>Access your NCSU Textbook Exchange account</p>
      
      {/* SAML2 Login Button */}
      <div style={{ marginBottom: '2rem', textAlign: 'center' }}>
        <button 
          onClick={handleSaml2Login}
          className="btn btn-primary"
          style={{ 
            width: '100%',
            backgroundColor: '#CC0000',
            borderColor: '#CC0000',
            fontSize: '16px',
            padding: '12px'
          }}
        >
          Login with NC State
        </button>
        <p style={{ marginTop: '10px', fontSize: '14px', color: '#666' }}>
          Recommended: Use your Unity ID to log in
        </p>
      </div>

      <div style={{ textAlign: 'center', margin: '20px 0' }}>
        <span style={{ color: '#666' }}>or</span>
      </div>
      
      {error && <div className="error">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="email">Email Address</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            placeholder="Enter your NCSU email"
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            placeholder="Enter your password"
          />
        </div>

        <button 
          type="submit" 
          className="btn btn-secondary"
          disabled={loading}
          style={{ width: '100%' }}
        >
          {loading ? 'Logging in...' : 'Login with Email'}
        </button>
      </form>

      <p style={{ textAlign: 'center', marginTop: '1rem' }}>
        Don't have an account? <Link to="/register">Register here</Link>
      </p>
    </div>
  );
};

export default Login;