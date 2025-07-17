import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import dayjs from 'dayjs';
import api from '../services/api';

const MyListings = ({ user }) => {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMyListings();
  }, []);

  const fetchMyListings = async () => {
    try {
      const data = await api.getMyListings();
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
      await api.deleteBook(bookId);
      setListings(prev => prev.filter(listing => listing.id !== bookId));
    } catch (err) {
      setError(err.message);
    }
  };

  // Rest of the component remains the same
  // ... (keep existing render logic and helper functions)
};

export default MyListings;