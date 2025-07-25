import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const ChatComponent = ({ userId, recipientId }) => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [connectionStatus, setConnectionStatus] = useState("Disconnected");
  const stompClient = useRef(null);
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
      console.error("No token found");
      setConnectionStatus("No token");
      return;
    }

    const wsUrl = `${API_URL}/ws?token=${encodeURIComponent(token)}`;
    console.log("Connecting to WebSocket at:", wsUrl);
    
    const client = new Client({
      webSocketFactory: () => {
        console.log("Creating SockJS connection...");
        return new SockJS(wsUrl);
      },
      reconnectDelay: 5000,
      debug: (str) => console.log("STOMP: " + str),
      onConnect: (frame) => {
        console.log("Connected to WebSocket:", frame);
        setConnectionStatus("Connected");
        
        client.subscribe("/topic/messages", (msg) => {
          console.log("Received message:", msg.body);
          const message = JSON.parse(msg.body);
          if (
            (message.senderId === userId && message.receiverId === recipientId) ||
            (message.senderId === recipientId && message.receiverId === userId)
          ) {
            setMessages((prev) => [...prev, message]);
          }
        });
      },
      onDisconnect: () => {
        console.log("Disconnected from WebSocket");
        setConnectionStatus("Disconnected");
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame);
        setConnectionStatus("Error: " + frame.headers.message);
      },
      onWebSocketError: (error) => {
        console.error("WebSocket error:", error);
        setConnectionStatus("WebSocket Error");
      }
    });
    
    stompClient.current = client;
    
    try {
      client.activate();
      setConnectionStatus("Connecting...");
    } catch (error) {
      console.error("Failed to activate client:", error);
      setConnectionStatus("Connection Failed");
    }

    const fetchMessages = async () => {
      try {
        const response = await fetch(`${API_URL}/api/messages/between/${userId}/${recipientId}`, {
          headers: { 
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        if (response.ok) {
          const data = await response.json();
          setMessages(data);
          console.log("Loaded message history:", data.length, "messages");
        } else {
          console.error("Failed to fetch messages:", response.status, response.statusText);
        }
      } catch (error) {
        console.error("Error fetching messages:", error);
      }
    };
    fetchMessages();

    return () => {
      if (stompClient.current) {
        console.log("Deactivating WebSocket connection");
        stompClient.current.deactivate();
      }
    };
  }, [userId, recipientId, token]);

  const getSenderName = (msg) => {
    if (msg.senderId === userId) return "Me";
    return msg.senderName || "Unknown";
  };

  const sendMessage = () => {
    if (!input.trim()) return;
    
    const msg = {
      senderId: userId,
      receiverId: recipientId,
      content: input.trim(),
    };
    
    if (stompClient.current && stompClient.current.connected) {
      console.log("Sending message:", msg);
      stompClient.current.publish({
        destination: "/app/chat",
        body: JSON.stringify(msg),
      });
      setInput("");
    } else {
      console.error("Cannot send message: WebSocket not connected");
      alert("WebSocket not connected. Please refresh the page.");
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '10px', padding: '5px', backgroundColor: '#f0f0f0' }}>
        Status: {connectionStatus}
      </div>
      <div style={{ height: 300, overflowY: "auto", border: '1px solid #ccc', padding: '10px' }}>
        {messages.map((m, i) => (
          <div key={i} style={{ marginBottom: '5px' }}>
            <b>{getSenderName(m)}:</b> {m.content}
            <small style={{ color: '#666', marginLeft: '10px' }}>
              {m.timestamp ? new Date(m.timestamp).toLocaleTimeString() : ''}
            </small>
          </div>
        ))}
      </div>
      <div style={{ marginTop: '10px' }}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={e => { if (e.key === "Enter") sendMessage(); }}
          placeholder="Type a message..."
          style={{ width: '80%', padding: '5px' }}
        />
        <button 
          onClick={sendMessage}
          disabled={connectionStatus !== "Connected"}
          style={{ width: '18%', padding: '5px', marginLeft: '2%' }}
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default ChatComponent;