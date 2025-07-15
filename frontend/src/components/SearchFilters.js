import React from 'react';

const SearchFilters = ({ 
  searchTerm, 
  setSearchTerm, 
  selectedCondition, 
  setSelectedCondition,
  priceRange,
  setPriceRange,
  onSearch 
}) => {
  const conditions = [
    { value: '', label: 'All Conditions' },
    { value: 'NEW', label: 'New' },
    { value: 'GOOD', label: 'Good' },
    { value: 'FAIR', label: 'Fair' },
    { value: 'POOR', label: 'Poor' }
  ];

  const handlePriceChange = (type, value) => {
    setPriceRange(prev => ({
      ...prev,
      [type]: value
    }));
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    onSearch({
      searchTerm,
      condition: selectedCondition,
      minPrice: priceRange.min,
      maxPrice: priceRange.max
    });
  };

  const handleReset = () => {
    setSearchTerm('');
    setSelectedCondition('');
    setPriceRange({ min: '', max: '' });
    onSearch({
      searchTerm: '',
      condition: '',
      minPrice: '',
      maxPrice: ''
    });
  };

  return (
    <div className="search-container">
      <form onSubmit={handleSearchSubmit}>
        <div className="search-filters">
          <div className="filter-group">
            <label htmlFor="search">Search Books</label>
            <input
              type="text"
              id="search"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Title, author, ISBN, or course code..."
            />
          </div>

          <div className="filter-group">
            <label htmlFor="condition">Condition</label>
            <select
              id="condition"
              value={selectedCondition}
              onChange={(e) => setSelectedCondition(e.target.value)}
            >
              {conditions.map(condition => (
                <option key={condition.value} value={condition.value}>
                  {condition.label}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label htmlFor="minPrice">Min Price ($)</label>
            <input
              type="number"
              id="minPrice"
              value={priceRange.min}
              onChange={(e) => handlePriceChange('min', e.target.value)}
              min="0"
              step="0.01"
              placeholder="0.00"
            />
          </div>

          <div className="filter-group">
            <label htmlFor="maxPrice">Max Price ($)</label>
            <input
              type="number"
              id="maxPrice"
              value={priceRange.max}
              onChange={(e) => handlePriceChange('max', e.target.value)}
              min="0"
              step="0.01"
              placeholder="999.99"
            />
          </div>
        </div>

        <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
          <button type="submit" className="btn btn-primary">
            Search Books
          </button>
          <button type="button" className="btn btn-secondary" onClick={handleReset}>
            Reset Filters
          </button>
        </div>
      </form>
    </div>
  );
};

export default SearchFilters;