import React, { useState } from 'react';
import BookCard from '../components/BookCard';
import SearchFilters from '../components/SearchFilters';
import api from '../services/api';
import '../styles/Home.css';

const Home = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCondition, setSelectedCondition] = useState('');
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });

  const handleLogin = () => {
    window.location.href = `${api.baseURL}/auth/login`;
  };

  const handleSearch = async (filters) => {
    try {
      setLoading(true);
      const data = await api.searchBooks(filters);
      setBooks(data);
    } catch (err) {
      setError('Error loading books. You may need to login to view listings.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="home">
      <div className="hero-section">
        <h1>Welcome to NCSU Textbook Exchange</h1>
        <p>Buy and sell textbooks directly with other NC State students</p>
      </div>

      <div className="features">
        <div className="feature-card">
          <h3>Save Money</h3>
          <p>Find affordable textbooks from fellow students</p>
        </div>
        <div className="feature-card">
          <h3>Easy Exchange</h3>
          <p>Connect directly with buyers and sellers on campus</p>
        </div>
        <div className="feature-card">
          <h3>Student Community</h3>
          <p>Join a trusted network of NCSU students</p>
        </div>
      </div>

      <div className="cta-section">
        <h2>Ready to get started?</h2>
        <button onClick={handleLogin} className="btn btn-primary login-btn">
          Login with NC State
        </button>
      </div>

      <SearchFilters 
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        selectedCondition={selectedCondition}
        setSelectedCondition={setSelectedCondition}
        priceRange={priceRange}
        setPriceRange={setPriceRange}
        onSearch={handleSearch}
      />

      {loading ? (
        <div className="loading">Loading...</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : (
        <div className="book-grid">
          {books.map(book => (
            <BookCard key={book.id} book={book} />
          ))}
        </div>
      )}
    </div>
  );
};

export default Home;