import React from 'react';
import { Link } from 'react-router-dom';

const Header = ({ user, onLogout }) => {
  return (
    <header className="header">
      <div className="header-content">
        <Link to="/" className="logo">
          📚 NCSU Textbook Exchange
        </Link>
        
        <nav className="nav-links">
          <Link to="/">Browse Books</Link>
          
          {user ? (
            <>
              <Link to="/post-listing">Post Listing</Link>
              <Link to="/my-listings">My Listings</Link>
              <Link to="/profile">Profile</Link>
              <div className="auth-links">
                <span>Welcome, {user.firstName}!</span>
                <button className="btn btn-secondary" onClick={onLogout}>
                  Logout
                </button>
              </div>
            </>
          ) : (
            <div className="auth-links">
              <Link to="/login" className="btn btn-secondary">
                Login
              </Link>
              <Link to="/register" className="btn btn-primary">
                Register
              </Link>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;