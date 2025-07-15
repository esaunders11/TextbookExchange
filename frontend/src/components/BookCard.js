import React from 'react';

const BookCard = ({ book }) => {
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

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const handleContactSeller = () => {
    // This would typically open a chat/messaging interface
    // For now, we'll just show an alert
    alert(`Contact ${book.seller?.firstName} ${book.seller?.lastName} about "${book.title}"`);
  };

  return (
    <div className="card book-card">
      <div className="book-info">
        <h3 className="book-title">{book.title}</h3>
        <p className="book-author">by {book.author}</p>
        
        {book.courseCode && (
          <p><strong>Course:</strong> {book.courseCode}</p>
        )}
        
        {book.courseName && (
          <p><strong>Class:</strong> {book.courseName}</p>
        )}
        
        {book.isbn && (
          <p><strong>ISBN:</strong> {book.isbn}</p>
        )}
        
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', margin: '1rem 0' }}>
          <span className="book-price">{formatPrice(book.price)}</span>
          <span className={`book-condition ${getConditionClass(book.condition)}`}>
            {book.condition}
          </span>
        </div>
        
        {book.description && (
          <p style={{ color: '#666', fontSize: '0.9rem', marginBottom: '1rem' }}>
            {book.description}
          </p>
        )}
        
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <small style={{ color: '#999' }}>
            Listed by {book.seller?.firstName} {book.seller?.lastName}
          </small>
          <button 
            className="btn btn-primary"
            onClick={handleContactSeller}
            style={{ padding: '0.5rem 1rem' }}
          >
            Contact Seller
          </button>
        </div>
        
        {book.createdAt && (
          <small style={{ color: '#999', display: 'block', marginTop: '0.5rem' }}>
            Posted: {new Date(book.createdAt).toLocaleDateString()}
          </small>
        )}
      </div>
    </div>
  );
};

export default BookCard;