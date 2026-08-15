import { createContext, useState, useEffect, useContext } from 'react';
import api from '../api/axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Check if user is already logged in (from localStorage) when app starts
    useEffect(() => {
        const storedUser = localStorage.getItem('eco_user');
        if (storedUser) {
            try {
                setUser(JSON.parse(storedUser));
            } catch (e) {
                console.error("Failed to parse stored user:", e);
                localStorage.removeItem('eco_user');
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await api.post('/auth/login', { email, password });
            
            // Backend returns: { token, id, email, fullName, role }
            const userData = response.data;
            
            setUser(userData);
            localStorage.setItem('eco_user', JSON.stringify(userData));
            return { success: true, user: userData };
        } catch (error) {
            console.error("Login error details:", error);
            let message = "Login failed. Please check your credentials.";
            
            if (error.response?.data?.error) {
                message = error.response.data.error;
            } else if (error.response?.data?.message) {
                message = error.response.data.message;
            } else if (error.code === 'ERR_NETWORK' || !error.response) {
                message = "Unable to connect to server. If the backend is waking up (cold start), please wait 15-30 seconds and try again.";
            } else if (error.response?.status === 401) {
                message = "Invalid email or password.";
            }
            
            return { 
                success: false, 
                message 
            };
        }
    };

    const register = async (email, password, fullName, role) => {
        try {
            const response = await api.post('/auth/register', { 
                email, 
                password, 
                fullName, 
                role 
            });
            return { success: true, data: response.data };
        } catch (error) {
            console.error("Registration error details:", error);
            let message = "Registration failed.";
            
            if (error.response?.data?.error) {
                message = error.response.data.error;
            } else if (error.response?.data?.message) {
                message = error.response.data.message;
            } else if (error.code === 'ERR_NETWORK' || !error.response) {
                message = "Unable to connect to server. Please wait a moment and try again.";
            }
            
            return { 
                success: false, 
                message 
            };
        }
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('eco_user');
        window.location.href = '/';
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);