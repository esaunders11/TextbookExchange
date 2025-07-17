import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const PostListing = ({ user }) => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    isbn: '',
    price: '',
    condition: '',
    description: '',
    courseCode: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Keep existing conditions array and handleChange function

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const cleanedData = sanitizeFormData(formData);
      await api.createBook({
        ...cleanedData,
        price: parseFloat(cleanedData.price)
      });

      setSuccess('Listing created successfully!');
      setFormData({
        title: '',
        author: '',
        isbn: '',
        price: '',
        condition: '',
        description: '',
        courseCode: ''
      });
      
      setTimeout(() => {
        navigate('/my-listings');
      }, 2000);
    } catch (err) {
      setError("Failed to create listing. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  // Rest of the component remains the same
  // ... (keep existing render logic and helper functions)
};

export default PostListing;