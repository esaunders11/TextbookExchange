import React from 'react';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const { loginWithSaml2 } = useAuth();

  const handleSaml2Login = () => {
    loginWithSaml2();
  };

  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h2>Login to TextbookExchange</h2>
      <div style={{ marginBottom: '20px' }}>
        <p>Use your NC State credentials to access the platform</p>
      </div>
      <button 
        onClick={handleSaml2Login}
        style={{
          padding: '12px 24px',
          fontSize: '16px',
          backgroundColor: '#CC0000',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          fontWeight: 'bold'
        }}
      >
        Login with NC State
      </button>
      <div style={{ marginTop: '20px', fontSize: '14px', color: '#666' }}>
        <p>
          This will redirect you to NC State's Shibboleth authentication service.
          After successful authentication, you'll be redirected back to the application.
        </p>
      </div>
    </div>
  );
};

export default Login;