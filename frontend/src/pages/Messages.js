import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MessageCard from '../components/MessageCard';


const Messages = ({ user }) => {
  const [receivedMessages, setReceivedMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const response = await fetch(`/api/messages/received/${user.id}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        const data = await response.json();
        const grouped = {};
        data.forEach(msg => {
          if (!grouped[msg.senderId] || new Date(msg.timestamp) > new Date(grouped[msg.senderId].timestamp)) {
            grouped[msg.senderId] = msg;
          }
        });
        const sorted = Object.values(grouped).sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
        setReceivedMessages(sorted);
      } catch (err) {
        setReceivedMessages([]);
      } finally {
        setLoading(false);
      }
    };
    if (user) fetchMessages();
  }, [user, token]);

  if (loading) return <div>Loading messages...</div>;
  if (!receivedMessages.length) return <div>No messages received.</div>;

  return (
    <div className="messages-list" style={{ maxWidth: 600, margin: '0 auto' }}>
      <h2>Received Messages</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {receivedMessages.map(msg => (
          <li key={msg.id} style={{ padding: 0, margin: 0 }}>
            <MessageCard
              senderId={msg.senderId}
              senderName={msg.senderName}
              lastMessage={msg.content}
              timestamp={msg.timestamp}
            />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Messages;
