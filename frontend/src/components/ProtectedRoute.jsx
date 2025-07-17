import React from 'react';
import { useAuth } from '../context/AuthContext';
import Login from './Login';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Login />;
  }

  return (
    <div>
      <div style={{ 
        padding: '10px 20px', 
        backgroundColor: '#f8f9fa', 
        borderBottom: '1px solid #dee2e6' 
      }}>
        <span>Welcome, {user?.firstName || user?.email || 'User'}</span>
        <button 
          onClick={() => window.location.href = '/api/auth/logout'}
          style={{
            marginLeft: '20px',
            padding: '5px 10px',
            backgroundColor: '#dc3545',
            color: 'white',
            border: 'none',
            borderRadius: '3px',
            cursor: 'pointer'
          }}
        >
          Logout
        </button>
      </div>
      {children}
    </div>
  );
};

export default ProtectedRoute;