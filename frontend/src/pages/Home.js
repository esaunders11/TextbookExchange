import React, { useState, useEffect } from 'react';
import BookCard from '../components/BookCard';
import SearchFilters from '../components/SearchFilters';

function getUserIdFromToken(token) {
  if (!token) return undefined;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub || payload.id || payload.userId || payload.uid;
  } catch (e) {
    return undefined;
  }
}

const Home = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCondition, setSelectedCondition] = useState('');
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUserId, setCurrentUserId] = useState(undefined);

  useEffect(() => {
    // Check if user is logged in
    const token = localStorage.getItem('token');
    setIsLoggedIn(!!token);
    setCurrentUserId(getUserIdFromToken(token));
    if (token) {
      fetchBooks();
    } else {
      setLoading(false);
    }
  }, []);

  const fetchBooks = async () => {
    try {
      const token = localStorage.getItem('token');
      setLoading(true);
      const response = await fetch('/api/books', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) {
        throw new Error('Failed to fetch books');
      }
      const data = await response.json();
      setBooks(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (filters) => {
    try {
      setLoading(true);
      const queryParams = new URLSearchParams();
      
      if (filters.searchTerm) queryParams.append('search', filters.searchTerm);
      if (filters.condition) queryParams.append('condition', filters.condition);
      if (filters.minPrice) queryParams.append('minPrice', filters.minPrice);
      if (filters.maxPrice) queryParams.append('maxPrice', filters.maxPrice);
      
      const response = await fetch(`/api/books/search?${queryParams}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      if (!response.ok) {
        throw new Error('Failed to search books');
      }
      const data = await response.json();
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
    <div>
      <h1>Browse Textbooks</h1>
      <p>Find used textbooks from NCSU students</p>
      
      {!isLoggedIn && (
        <div className="welcome-message card">
          <h2>Welcome to the NCSU Textbook Exchange!</h2>
          <p>
            This is a student-to-student platform where NC State students can buy and sell 
            used textbooks at affordable prices. Join our community to:
          </p>
          <ul>
            <li>Browse textbooks from fellow students</li>
            <li>Save money on required course materials</li>
            <li>Sell your books when you're done with them</li>
            <li>Connect with other students in your classes</li>
          </ul>
          <p>
            <strong>Please log in or create an account to start buying and selling textbooks!</strong>
          </p>
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
        isLoggedIn={isLoggedIn}
      />
      
      {isLoggedIn ? (
        filteredBooks.length === 0 ? (
          <div className="card">
            <p>No books found matching your criteria.</p>
          </div>
        ) : (
          <div className="card-grid">
            {filteredBooks.map(book => (
              <BookCard key={book.id} book={book} currentUserId={currentUserId} />
            ))}
          </div>
        )
      ) : (
        <div className="card">
          <p>Please log in to view available textbooks.</p>
        </div>
      )}
    </div>
  );
};

export default Home;