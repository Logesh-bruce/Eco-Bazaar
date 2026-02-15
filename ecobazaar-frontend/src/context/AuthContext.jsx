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
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await api.post('/auth/login', { email, password });
            
            // Backend returns: { id, email, fullName, role }
            const userData = response.data;
            
            setUser(userData);
            localStorage.setItem('eco_user', JSON.stringify(userData));
            return { success: true };
        } catch (error) {
            console.error("Login failed:", error);
            return { 
                success: false, 
                message: error.response?.data?.error || "Login failed" 
            };
        }
    };

    const register = async (email, password, fullName, role) => {
        try {
            await api.post('/auth/register', { 
                email, 
                password, 
                fullName, 
                role 
            });
            return { success: true };
        } catch (error) {
            return { 
                success: false, 
                message: error.response?.data?.error || "Registration failed" 
            };
        }
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('eco_user');
        // Optional: Redirect to home page
        window.location.href = '/';
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);