// services/api.js
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

class ApiService {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const token = localStorage.getItem('token');

    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` }),
        ...options.headers,
      },
      ...options,
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  // Authentication
  async login(credentials) {
    return this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  }

  async register(userData) {
    return this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  }

  async verifyToken() {
    return this.request('/auth/verify', {
      method: 'GET',
    });
  }

  // Books
  async getBooks() {
    return this.request('/books', {
      method: 'GET',
    });
  }

  async searchBooks(params) {
    const queryString = new URLSearchParams(params).toString();
    return this.request(`/books/search?${queryString}`, {
      method: 'GET',
    });
  }

  async getBookById(id) {
    return this.request(`/books/${id}`, {
      method: 'GET',
    });
  }

  async createBook(bookData) {
    return this.request('/books', {
      method: 'POST',
      body: JSON.stringify(bookData),
    });
  }

  async updateBook(id, bookData) {
    return this.request(`/books/${id}`, {
      method: 'PUT',
      body: JSON.stringify(bookData),
    });
  }

  async deleteBook(id) {
    return this.request(`/books/${id}`, {
      method: 'DELETE',
    });
  }

  async getMyListings() {
    return this.request('/books/my-listings', {
      method: 'GET',
    });
  }

  // User Profile
  async getUserProfile() {
    return this.request('/users/profile', {
      method: 'GET',
    });
  }

  async updateUserProfile(userData) {
    return this.request('/users/profile', {
      method: 'PUT',
      body: JSON.stringify(userData),
    });
  }

  // Future: Chat/Messaging
  async getConversations() {
    return this.request('/messages/conversations', {
      method: 'GET',
    });
  }

  async getMessages(conversationId) {
    return this.request(`/messages/conversations/${conversationId}`, {
      method: 'GET',
    });
  }

  async sendMessage(conversationId, messageData) {
    return this.request(`/messages/conversations/${conversationId}`, {
      method: 'POST',
      body: JSON.stringify(messageData),
    });
  }

  async startConversation(sellerId, bookId) {
    return this.request('/messages/conversations', {
      method: 'POST',
      body: JSON.stringify({ sellerId, bookId }),
    });
  }
}

export default new ApiService();