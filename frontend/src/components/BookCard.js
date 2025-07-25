import React from 'react';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';

const BookCard = ({ book, currentUserId }) => {
  const navigate = useNavigate();

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

  const handleContactSeller = (e) => {
    e.preventDefault();
    if (book.seller?.id && currentUserId) {
      navigate(`/chat/${book.seller.id}`);
    }
  };

  return (
    <div className="card book-card">
      {console.log('Image URL:', book.imageUrl)}
      <div className="book-info">
        <h3 className="book-title">{book.title}</h3>
        {book.imageUrl && (
          <div className="book-image-container" style={{ textAlign: 'center', marginBottom: '1rem' }}>
            <img
              src={book.imageUrl}
              alt={`Cover of ${book.title}`}
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
            type="button"
            disabled={!book.seller?.id || !currentUserId}
          >
            Contact Seller
          </button>
        </div>
        
        {book.createdAt && (
          <small style={{ color: '#999', display: 'block', marginTop: '0.5rem' }}>
            Posted: {dayjs(book.createdAt).format('MMM D, YYYY')}
          </small>
        )}
      </div>
    </div>
  );
};

export default BookCard;