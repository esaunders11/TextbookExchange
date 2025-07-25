import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

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
  const [imageFile, setImageFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const API_BASE_URL = process.env.REACT_APP_API_URL;

  const conditions = [
    { value: 'NEW', label: 'New' },
    { value: 'GOOD', label: 'Good' },
    { value: 'FAIR', label: 'Fair' },
    { value: 'POOR', label: 'Poor' }
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    setImageFile(e.target.files[0]);
  };

  const validateImage = (file) => {
    if (!file) return false;
    const allowedTypes = ['image/jpeg', 'image/png'];
    return allowedTypes.includes(file.type);
  };

  const sanitizeFormData = (data) => ({
    ...data,
    title: data.title.trim(),
    author: data.author.trim(),
    isbn: data.isbn.trim(),
    price: data.price,
    condition: data.condition.trim(),
    description: data.description.trim(),
    courseCode: data.courseCode.replace(/\s+/g, '').toUpperCase(),
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    if (!imageFile) {
      setError('A book image is required.');
      setLoading(false);
      return;
    }
    if (!validateImage(imageFile)) {
      setError('Please upload a valid image (JPEG or PNG).');
      setLoading(false);
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const cleanedData = sanitizeFormData(formData);

      const formDataToSend = new FormData();
      formDataToSend.append('request', new Blob([JSON.stringify({
        ...cleanedData,
        price: parseFloat(cleanedData.price)
      })], { type: 'application/json' }));

      if (imageFile) {
        formDataToSend.append('image', imageFile);
      }

      const response = await fetch(`${API_BASE_URL}/api/books/post-listing`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formDataToSend
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to create listing');
      }

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
      setImageFile(null);
      
      setTimeout(() => {
        navigate('/my-listings');
      }, 2000);
    } catch (err) {
      setError("Failed to create listing. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container" style={{ maxWidth: '600px' }}>
      <h2>Post a New Textbook</h2>
      <p>List your textbook for other NCSU students</p>
      
      {error && <div className="error">{error}</div>}
      {success && <div className="success">{success}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">Book Title *</label>
          <input
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
            placeholder="Enter the book title"
          />
        </div>

        <div className="form-group">
          <label htmlFor="author">Author *</label>
          <input
            type="text"
            id="author"
            name="author"
            value={formData.author}
            onChange={handleChange}
            required
            placeholder="Enter the author's name"
          />
        </div>

        <div className="form-group">
          <label htmlFor="isbn">ISBN</label>
          <input
            type="text"
            id="isbn"
            name="isbn"
            value={formData.isbn}
            onChange={handleChange}
            placeholder="Enter ISBN (optional)"
          />
        </div>

        <div className="form-group">
          <label htmlFor="courseCode">Course Code</label>
          <input
            type="text"
            id="courseCode"
            name="courseCode"
            value={formData.courseCode}
            onChange={handleChange}
            placeholder="e.g., CSC 116, ENG 101"
          />
        </div>

        <div className="form-group">
          <label htmlFor="price">Price ($) *</label>
          <input
            type="number"
            id="price"
            name="price"
            value={formData.price}
            onChange={handleChange}
            required
            min="0"
            step="0.01"
            placeholder="Enter price"
          />
        </div>

        <div className="form-group">
          <label htmlFor="condition">Condition *</label>
          <select
            id="condition"
            name="condition"
            value={formData.condition}
            onChange={handleChange}
            required
          >
            <option value="">Select condition</option>
            {conditions.map(condition => (
              <option key={condition.value} value={condition.value}>
                {condition.label}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Additional details about the book (highlighting, wear, etc.)"
            rows="4"
          />
        </div>

        <div className="form-group">
          <label htmlFor="image">Upload Image</label>
          <input
            type="file"
            id="image"
            name="image"
            accept="image/*"
            onChange={handleFileChange}
            required
          />
        </div>

        <button 
          type="submit" 
          className="btn btn-primary"
          disabled={loading}
          style={{ width: '100%' }}
        >
          {loading ? 'Creating Listing...' : 'Post Listing'}
        </button>
      </form>
    </div>
  );
};

export default PostListing;