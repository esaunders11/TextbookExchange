import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/Header';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import PostListing from './pages/PostListing';
import MyListings from './pages/MyListings';
import Profile from './pages/Profile';
import Verification from './pages/Verification';
import VerifyEmail from './services/VerifyEmail';
import Chat from './pages/Chat';
import Messages from './pages/Messages';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on app load
    const token = localStorage.getItem('token');
    if (token) {
      // Verify token with backend
      fetch('/api/auth/user', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(response => {
        if (response.status === 401) {
          setUser(null);
          localStorage.removeItem('token');
          return;
        }
        if (response.ok) {
          return response.json();
        }
        throw new Error('Token invalid');
      })
      .then(userData => {
        setUser(userData);
      })
      .catch(error => {
        console.error('Token verification failed:', error);
        localStorage.removeItem('token');
      })
      .finally(() => {
        setLoading(false);
      });
    } else {
      setLoading(false);
    }
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('token');
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <Router>
      <div className="App">
        <Header user={user} onLogout={handleLogout} />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route 
              path="/login" 
              element={user ? <Navigate to="/" /> : <Login onLogin={handleLogin} />} 
            />
            <Route 
              path="/register" 
              element={user ? <Navigate to="/" /> : <Register onLogin={handleLogin} />} 
            />
            <Route 
              path="/post-listing" 
              element={user ? <PostListing user={user} /> : <Navigate to="/login" />} 
            />
            <Route 
              path="/my-listings" 
              element={user ? <MyListings user={user} /> : <Navigate to="/login" />} 
            />
            <Route 
              path="/profile" 
              element={user ? <Profile user={user} /> : <Navigate to="/login" />} 
            />
            <Route
              path="/messages"
              element={user ? <Messages user={user} /> : <Navigate to="/login" />}
            />
            <Route path="/verification" element={<Verification />} />
            <Route path="/verify" element={<VerifyEmail />} />
            <Route path="/chat/:recipientId" element={<Chat currentUserId={user?.id} />} />
            
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;