import React, { useState, useEffect } from 'react';
import BookCard from '../components/BookCard';
import SearchFilters from '../components/SearchFilters';

const Home = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCondition, setSelectedCondition] = useState('');
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/books');
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
      
      const response = await fetch(`/api/books/search?${queryParams}`);
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
      
      <SearchFilters 
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        selectedCondition={selectedCondition}
        setSelectedCondition={setSelectedCondition}
        priceRange={priceRange}
        setPriceRange={setPriceRange}
        onSearch={handleSearch}
      />
      
      {filteredBooks.length === 0 ? (
        <div className="card">
          <p>No books found matching your criteria.</p>
        </div>
      ) : (
        <div className="card-grid">
          {filteredBooks.map(book => (
            <BookCard key={book.id} book={book} />
          ))}
        </div>
      )}
    </div>
  );
};

export default Home;