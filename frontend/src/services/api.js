const api = {
    baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',

    async verifySession() {
        try {
            const response = await fetch(`${this.baseURL}/auth/verify`, {
                credentials: 'include'
            });
            
            if (!response.ok) {
                return null; // Return null instead of throwing error for unauthenticated users
            }
            
            return await response.json();
        } catch (error) {
            return null; // Return null for any errors instead of throwing
        }
    },

    async searchBooks(filters) {
        const response = await fetch(`${this.baseURL}/books/search`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(filters),
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to search books');
        }

        return await response.json();
    },

    async logout() {
        const response = await fetch(`${this.baseURL}/auth/logout`, {
            method: 'POST',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Logout failed');
        }
    }
};

export default api;