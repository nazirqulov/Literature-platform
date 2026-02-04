// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Axios-like API client
const api = {
    async request(method, url, data = null, customHeaders = {}) {
        const token = localStorage.getItem('token');

        const headers = {
            'Content-Type': 'application/json',
            ...customHeaders
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            method,
            headers
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            config.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(`${API_BASE_URL}${url}`, config);

            if (!response.ok) {
                const error = await response.json();
                throw {
                    response: {
                        status: response.status,
                        data: error
                    }
                };
            }

            const responseData = await response.json();
            return { data: responseData };

        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    get(url, headers = {}) {
        return this.request('GET', url, null, headers);
    },

    post(url, data, headers = {}) {
        return this.request('POST', url, data, headers);
    },

    put(url, data, headers = {}) {
        return this.request('PUT', url, data, headers);
    },

    delete(url, headers = {}) {
        return this.request('DELETE', url, null, headers);
    }
};

// API Endpoints
const API = {
    // Auth
    login: (data) => api.post('/login', data),
    register: (data) => api.post('/register', data),
    verifyEmail: (data) => api.post('/verify-email', data),
    forgotPassword: (data) => api.post('/forgot-password', data),
    resetPassword: (data) => api.post('/reset-password', data),

    // User
    getMe: () => api.get('/users/me'),
    updateProfile: (data) => api.put('/users/me', data),
    changePassword: (data) => api.post('/users/me/change-password', data),

    // Books
    getBooks: (params = '') => api.get(`/books${params}`),
    getBook: (id) => api.get(`/books/${id}`),
    searchBooks: (keyword) => api.get(`/books/search?keyword=${keyword}`),
    getFeaturedBooks: () => api.get('/books/featured'),
    getPopularBooks: () => api.get('/books/popular'),
    getLatestBooks: () => api.get('/books/latest'),
    getTopRatedBooks: () => api.get('/books/top-rated'),

    // Favorites
    getFavorites: () => api.get('/favorites'),
    addFavorite: (bookId) => api.post(`/favorites/${bookId}`),
    removeFavorite: (bookId) => api.delete(`/favorites/${bookId}`),
    checkFavorite: (bookId) => api.get(`/favorites/check/${bookId}`),

    // Reviews
    getBookReviews: (bookId) => api.get(`/reviews/book/${bookId}`),
    addReview: (data) => api.post('/reviews', data),
    updateReview: (id, data) => api.put(`/reviews/${id}`, data),
    deleteReview: (id) => api.delete(`/reviews/${id}`),
    getMyReviews: () => api.get('/reviews/my-reviews'),

    // Authors
    getAuthors: () => api.get('/authors'),
    getAuthor: (id) => api.get(`/authors/${id}`),

    // Categories
    getCategories: () => api.get('/categories'),
    getCategory: (id) => api.get(`/categories/${id}`),

    // AI
    chatWithAI: (data) => api.post('/ai/chat', data),
    analyzeBook: (bookId) => api.get(`/ai/analyze/${bookId}`),
    summarizeBook: (bookId) => api.get(`/ai/summarize/${bookId}`),
    getRecommendations: () => api.get('/ai/recommendations')
};

// Export for use in other files
window.api = api;
window.API = API;
