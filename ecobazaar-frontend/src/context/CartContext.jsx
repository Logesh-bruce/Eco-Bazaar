import { createContext, useState, useContext, useEffect } from 'react';
import { useAuth } from './AuthContext';
import api from '../api/axios';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const { user } = useAuth();
    const [cartCount, setCartCount] = useState(0);

    // 1. Function to fetch the real count from the backend
    const fetchCartCount = async () => {
        if (!user) {
            setCartCount(0);
            return;
        }
        try {
            const response = await api.get(`/cart/${user.id}`);
            const items = response.data.items || [];
            // Sum up the quantity of all items
            const totalQty = items.reduce((acc, item) => acc + item.quantity, 0);
            setCartCount(totalQty);
        } catch (error) {
            console.error("Failed to update cart count", error);
        }
    };

    // 2. Fetch count whenever the user changes (login/logout)
    useEffect(() => {
        fetchCartCount();
    }, [user]);

    return (
        <CartContext.Provider value={{ cartCount, fetchCartCount }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => useContext(CartContext);