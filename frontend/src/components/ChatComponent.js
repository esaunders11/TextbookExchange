import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const API_URL = 'http://localhost:8080/api';

const ChatComponent = ({ userId, recipientId }) => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const stompClient = useRef(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_URL}/ws`),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe("/topic/messages", (msg) => {
          const message = JSON.parse(msg.body);
          if (
            (message.senderId === userId && message.receiverId === recipientId) ||
            (message.senderId === recipientId && message.receiverId === userId)
          ) {
            setMessages((prev) => [...prev, message]);
          }
        });
      },
    });
    stompClient.current = client;
    client.activate();

    // Fetch history
    fetch(`${API_URL}/messages/${userId}/${recipientId}`)
      .then((r) => r.json())
      .then(setMessages);

    return () => {
      if (stompClient.current) stompClient.current.deactivate();
    };
  }, [userId, recipientId]);

  const sendMessage = () => {
    const msg = {
      senderId: userId,
      receiverId: recipientId,
      content: input,
    };
    if (stompClient.current && stompClient.current.connected) {
      stompClient.current.publish({
        destination: "/app/chat",
        body: JSON.stringify(msg),
      });
    }
    setInput("");
  };

  return (
    <div>
      <div style={{ height: 300, overflowY: "auto" }}>
        {messages.map((m, i) => (
          <div key={i}>
            <b>{m.senderId === userId ? "Me" : "Them"}:</b> {m.content}
          </div>
        ))}
      </div>
      <input
        value={input}
        onChange={(e) => setInput(e.target.value)}
        onKeyDown={e => { if (e.key === "Enter") sendMessage(); }}
      />
      <button onClick={sendMessage}>Send</button>
    </div>
  );
};

export default ChatComponent;