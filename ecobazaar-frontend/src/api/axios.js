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
                console.warn('Could not parse eco_user from localStorage:', e);
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

/**
 * Response interceptor — handles 401 Unauthorized globally for authenticated API calls.
 * IMPORTANT: Excludes /auth/login and /auth/register so failed authentication attempts
 * do NOT trigger a full page reload or wipe the form input fields.
 */
api.interceptors.response.use(
    (response) => response,
    (error) => {
        const isAuthEndpoint = error.config?.url?.includes('/auth/login') || error.config?.url?.includes('/auth/register');
        if (error.response?.status === 401 && !isAuthEndpoint) {
            localStorage.removeItem('eco_user');
            if (window.location.pathname !== '/login') {
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;