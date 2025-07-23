import React from "react";
import { useParams } from "react-router-dom";
import ChatComponent from "../components/ChatComponent";

const Chat = ({ currentUserId }) => {
  const { recipientId } = useParams();
  return (
    <div className="chat-page">
      <h2>Chat</h2>
      <ChatComponent userId={currentUserId} recipientId={parseInt(recipientId, 10)} />
    </div>
  );
};

export default Chat;
