import React from 'react';
import { useNavigate } from 'react-router-dom';

const MessageCard = ({ senderId, senderName, lastMessage, timestamp }) => {
  const navigate = useNavigate();

  return (
    <div
      className="message-card"
      style={{
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0.75rem 1rem',
        borderBottom: '1px solid #eee',
        cursor: 'pointer',
        background: '#fff',
        transition: 'background 0.2s',
      }}
      onClick={() => navigate(`/chat/${senderId}`)}
    >
      <div style={{ flex: 1 }}>
        <div style={{ fontWeight: 'bold', fontSize: '1rem' }}>{senderName || senderId}</div>
        <div style={{ color: '#555', fontSize: '0.95rem', marginTop: 2 }}>{lastMessage}</div>
      </div>
      <div style={{ fontSize: '0.8rem', color: '#999', marginLeft: '1rem', whiteSpace: 'nowrap' }}>
        {new Date(timestamp).toLocaleString()}
      </div>
    </div>
  );
};

export default MessageCard;
