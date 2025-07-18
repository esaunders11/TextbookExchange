import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/Header';
import Home from './pages/Home';
import Profile from './pages/Profile';
import PostListing from './pages/PostListing';
import MyListings from './pages/MyListings';
import './App.css';
import api from './services/api';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const userData = await api.verifySession();
      // Only set user if we got valid data back
      if (userData) {
        setUser(userData);
      } else {
        setUser(null);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = () => {
    window.location.href = `${api.baseURL}/auth/login`;
  };

  const handleLogout = async () => {
    try {
      await api.logout();
      setUser(null);
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <Router>
      <div className="App">
        <Header user={user} onLogout={handleLogout} onLogin={handleLogin} />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route 
              path="/post-listing" 
              element={user ? <PostListing user={user} /> : <Navigate to="/" />} 
            />
            <Route 
              path="/my-listings" 
              element={user ? <MyListings user={user} /> : <Navigate to="/" />} 
            />
            <Route 
              path="/profile" 
              element={user ? <Profile user={user} /> : <Navigate to="/" />} 
            />
            <Route 
              path="/auth/success" 
              element={<AuthCallback onSuccess={checkAuthStatus} />} 
            />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

function AuthCallback({ onSuccess }) {
  useEffect(() => {
    onSuccess();
    window.location.href = '/';
  }, [onSuccess]);

  return <div className="loading">Authenticating...</div>;
}

export default App;