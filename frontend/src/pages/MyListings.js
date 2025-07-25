import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import dayjs from 'dayjs';

const MyListings = ({ user }) => {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMyListings();
  }, []);

  const fetchMyListings = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/books/my-listings', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch listings');
      }

      const data = await response.json();
      setListings(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (bookId) => {
    if (!window.confirm('Are you sure you want to delete this listing?')) {
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/api/books/${bookId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to delete listing');
      }

      setListings(prev => prev.filter(listing => listing.id !== bookId));
    } catch (err) {
      setError(err.message);
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const getConditionClass = (condition) => {
    switch (condition?.toLowerCase()) {
      case 'new':
        return 'condition-new';
      case 'good':
        return 'condition-good';
      case 'fair':
        return 'condition-fair';
      case 'poor':
        return 'condition-poor';
      default:
        return 'condition-good';
    }
  };

  if (loading) {
    return <div className="loading">Loading your listings...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <h1>My Listings</h1>
        <Link to="/post-listing" className="btn btn-primary">
          Post New Listing
        </Link>
      </div>

      {listings.length === 0 ? (
        <div className="card">
          <h3>No listings yet</h3>
          <p>You haven't posted any textbooks for sale yet.</p>
          <Link to="/post-listing" className="btn btn-primary">
            Post Your First Listing
          </Link>
        </div>
      ) : (
        <div className="card-grid">
          {listings.map(listing => (
            <div key={listing.id} className="card">
              <div className="book-info">
                <h3 className="book-title">{listing.title}</h3>
                {listing.imageUrl && (
                  <div className="book-image-container" style={{ textAlign: 'center', marginBottom: '1rem' }}>
                    {console.log('Image URL:', listing.imageUrl)}
                    <img
                      src={listing.imageUrl}
                      style={{
                        maxWidth: '200px',
                        maxHeight: '250px',
                        objectFit: 'cover',
                        borderRadius: '8px',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
                      }}
                    />
                  </div>
                )}
                <p className="book-author">by {listing.author}</p>
                
                {listing.courseCode && (
                  <p><strong>Course:</strong> {listing.courseCode}</p>
                )}
                
                {listing.courseName && (
                  <p><strong>Class:</strong> {listing.courseName}</p>
                )}
                
                {listing.isbn && (
                  <p><strong>ISBN:</strong> {listing.isbn}</p>
                )}
                
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', margin: '1rem 0' }}>
                  <span className="book-price">{formatPrice(listing.price)}</span>
                  <span className={`book-condition ${getConditionClass(listing.condition)}`}>
                    {listing.condition}
                  </span>
                </div>
                
                {listing.description && (
                  <p style={{ color: '#666', fontSize: '0.9rem', marginBottom: '1rem' }}>
                    {listing.description}
                  </p>
                )}
                
                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}>
                  <button 
                    className="btn btn-secondary"
                    onClick={() => alert('Edit functionality coming soon!')}
                    style={{ flex: 1 }}
                  >
                    Edit
                  </button>
                  <button 
                    className="btn btn-danger"
                    onClick={() => handleDelete(listing.id)}
                    style={{ flex: 1 }}
                  >
                    Delete
                  </button>
                </div>
                
                <small style={{ color: '#999', display: 'block', marginTop: '0.5rem' }}>
                  Posted: {dayjs(listing.createdAt).format('MMM D, YYYY')}
                </small>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyListings;