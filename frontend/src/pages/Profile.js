import React, { useState } from 'react';

const Profile = ({ user }) => {
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: user.firstName || '',
    lastName: user.lastName || '',
    email: user.email || ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

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
    setSuccess('');

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/users/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to update profile');
      }

      setSuccess('Profile updated successfully!');
      setEditing(false);
    } catch (err) {
      setError("Unable to update profile. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      email: user.email || ''
    });
    setEditing(false);
    setError('');
    setSuccess('');
  };

  return (
    <div className="form-container">
      <h2>My Profile</h2>
      
      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}
      
      {editing ? (
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="firstName">First Name</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="lastName">Last Name</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              disabled
              style={{ backgroundColor: '#f5f5f5', color: '#666' }}
            />
            <small style={{ color: '#666' }}>Email cannot be changed</small>
          </div>

          <div style={{ display: 'flex', gap: '1rem' }}>
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading}
              style={{ flex: 1 }}
            >
              {loading ? 'Saving...' : 'Save Changes'}
            </button>
            <button 
              type="button" 
              className="btn btn-secondary"
              onClick={handleCancel}
              style={{ flex: 1 }}
            >
              Cancel
            </button>
          </div>
        </form>
      ) : (
        <div>
          <div className="form-group">
            <label>First Name</label>
            <p style={{ padding: '0.75rem', border: '1px solid #ddd', borderRadius: '4px', backgroundColor: '#f9f9f9' }}>
              {user.firstName}
            </p>
          </div>

          <div className="form-group">
            <label>Last Name</label>
            <p style={{ padding: '0.75rem', border: '1px solid #ddd', borderRadius: '4px', backgroundColor: '#f9f9f9' }}>
              {user.lastName}
            </p>
          </div>

          <div className="form-group">
            <label>Email Address</label>
            <p style={{ padding: '0.75rem', border: '1px solid #ddd', borderRadius: '4px', backgroundColor: '#f9f9f9' }}>
              {user.email}
            </p>
          </div>

          <button 
            className="btn btn-primary"
            onClick={() => setEditing(true)}
            style={{ width: '100%' }}
          >
            Edit Profile
          </button>
        </div>
      )}
    </div>
  );
};

export default Profile;