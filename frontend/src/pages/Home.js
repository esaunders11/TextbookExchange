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
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);

  const handleLogin = () => {
    window.location.href = `${api.baseURL}/auth/login`;
  };

  const handleSearch = async (filters) => {
    if (!showLoginPrompt) {
      setShowLoginPrompt(true);
      return;
    }

    try {
      setLoading(true);
      const data = await api.searchBooks(filters);
      setBooks(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredBooks = books.filter(book => {
    const matchesSearch = !searchTerm || 
      book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.isbn.includes(searchTerm);
    
    const matchesCondition = !selectedCondition || book.condition === selectedCondition;
    
    const matchesPrice = 
      (!priceRange.min || book.price >= parseFloat(priceRange.min)) &&
      (!priceRange.max || book.price <= parseFloat(priceRange.max));
    
    return matchesSearch && matchesCondition && matchesPrice;
  });

  if (loading) {
    return <div className="loading">Loading books...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  return (
    <div className="home-container">
      <div className="welcome-banner">
        <h1>Welcome to NCSU Textbook Exchange</h1>
        <p>A student-to-student platform for buying and selling textbooks</p>
      </div>

      <div className="features-grid">
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

      {showLoginPrompt && (
        <div className="login-prompt">
          <p>Please log in to search and view textbook listings.</p>
          <button onClick={handleLogin} className="btn btn-primary">
            Login with NC State
          </button>
        </div>
      )}

      <SearchFilters 
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        selectedCondition={selectedCondition}
        setSelectedCondition={setSelectedCondition}
        priceRange={priceRange}
        setPriceRange={setPriceRange}
        onSearch={handleSearch}
      />
      
      {showLoginPrompt ? (
        <div className="login-required-message">
          <h3>Browse Available Textbooks</h3>
          <p>Log in to view and search through available textbooks from NCSU students.</p>
        </div>
      ) : (
        filteredBooks.length > 0 ? (
          <div className="card-grid">
            {filteredBooks.map(book => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
        ) : (
          <div className="no-results">
            <p>No books found matching your criteria.</p>
          </div>
        )
      )}
    </div>
  );
};

export default Home;