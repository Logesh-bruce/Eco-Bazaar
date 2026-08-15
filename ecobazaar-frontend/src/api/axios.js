import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * Request interceptor — automatically attaches the JWT token from localStorage
 * to every outgoing request as "Authorization: Bearer <token>".
 *
 * This is the fix for the 403 Forbidden on POST /api/products/add:
 * without this, the token was never sent and the backend rejected all
 * authenticated endpoints.
 */
api.interceptors.request.use(
    (config) => {
        const storedUser = localStorage.getItem('eco_user');
        if (storedUser) {
            try {
                const userData = JSON.parse(storedUser);
                if (userData?.token) {
                    config.headers['Authorization'] = `Bearer ${userData.token}`;
                }
            } catch (e) {
                // Ignore malformed localStorage data
                console.warn('Could not parse eco_user from localStorage:', e);
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

/**
 * Response interceptor — handles 401 Unauthorized globally.
 * If a token is expired or invalid, clear localStorage and redirect to login.
 */
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('eco_user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;