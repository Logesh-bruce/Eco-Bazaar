import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Trash2, ShoppingBag, ArrowRight, Leaf } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import api from '../api/axios';
import { getImageSrc, handleImageError } from '../utils/imageUtils';

const Cart = () => {
    const { user } = useAuth();
    const { fetchCartCount } = useCart();
    const navigate = useNavigate();
    
    const [cartItems, setCartItems] = useState([]);
    const [totals, setTotals] = useState({ price: 0, carbon: 0 });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) {
            navigate('/login');
            return;
        }

        const fetchCart = async () => {
            try {
                const response = await api.get(`/cart/${user.id}`);
                const items = response.data?.items || [];
                setCartItems(items);
                calculateTotals(items);
            } catch (error) {
                console.error("Failed to fetch cart", error);
            } finally {
                setLoading(false);
            }
        };

        fetchCart();
    }, [user, navigate]);

    const calculateTotals = (items) => {
        const totalP = items.reduce((acc, item) => acc + (item.price * item.quantity), 0);
        const totalC = items.reduce((acc, item) => acc + (item.carbonFootprint * item.quantity), 0);
        setTotals({ price: totalP, carbon: totalC });
    };

    const handleRemoveItem = async (itemId) => {
        try {
            await api.delete(`/cart/${user.id}/items/${itemId}`);
            
            const newItems = cartItems.filter(item => item.id !== itemId);
            setCartItems(newItems);
            calculateTotals(newItems);

            await fetchCartCount();
        } catch (error) {
            alert("Failed to remove item");
        }
    };

    if (loading) return <div className="text-center py-20">Loading Cart...</div>;

    if (cartItems.length === 0) {
        return (
            <div className="text-center py-20">
                <div className="bg-gray-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6">
                    <ShoppingBag className="text-gray-400 w-10 h-10" />
                </div>
                <h2 className="text-2xl font-bold text-gray-800">Your cart is empty</h2>
                <p className="text-gray-500 mb-8">Looks like you haven't added anything sustainable yet.</p>
                <Link to="/" className="bg-eco-green text-white px-6 py-3 rounded-full font-bold hover:bg-eco-dark transition">
                    Start Shopping
                </Link>
            </div>
        );
    }

    return (
        <div className="max-w-6xl mx-auto">
            <h1 className="text-3xl font-bold mb-8 flex items-center gap-3">
                <ShoppingBag /> Your Cart
            </h1>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div className="lg:col-span-2 space-y-4">
                    {cartItems.map(item => {
                        const imgSrc = getImageSrc(item.imageUrl || item.image || item.imageBase64);
                        return (
                            <div key={item.id} className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex gap-4 items-center">
                                <img 
                                    src={imgSrc} 
                                    alt={item.productName} 
                                    onError={handleImageError}
                                    className="w-24 h-24 object-cover rounded-md border" 
                                />
                                
                                <div className="flex-grow">
                                    <div className="flex justify-between items-start">
                                        <h3 className="font-bold text-lg text-gray-800">{item.productName}</h3>
                                        <button onClick={() => handleRemoveItem(item.id)} className="text-gray-400 hover:text-red-500 p-1">
                                            <Trash2 size={20} />
                                        </button>
                                    </div>
                                    <p className="text-sm text-gray-500 mb-2">{item.category}</p>
                                    
                                    <div className="flex items-center gap-4">
                                        <span className="font-bold text-lg">${item.price.toFixed(2)}</span>
                                        <span className="text-xs bg-gray-100 px-2 py-1 rounded">Qty: {item.quantity}</span>
                                    </div>
                                </div>

                                <div className="text-right min-w-[100px] border-l pl-4">
                                    <div className="flex items-center justify-end gap-1 text-eco-green font-bold text-sm">
                                        <Leaf size={14} />
                                        {item.carbonFootprint} kg
                                    </div>
                                    <span className="text-xs text-gray-400">CO₂ impact</span>
                                </div>
                            </div>
                        );
                    })}
                </div>

                <div className="lg:col-span-1">
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 sticky top-24">
                        <h3 className="text-xl font-bold mb-6">Order Summary</h3>
                        
                        <div className="space-y-3 mb-6">
                            <div className="flex justify-between text-gray-600">
                                <span>Subtotal</span>
                                <span className="font-bold text-gray-800">${totals.price.toFixed(2)}</span>
                            </div>
                            <div className="flex justify-between text-gray-600">
                                <span>Total Carbon</span>
                                <span className="font-bold text-eco-green flex items-center gap-1">
                                    <Leaf size={14} /> {totals.carbon.toFixed(2)} kg CO₂
                                </span>
                            </div>
                            <div className="flex justify-between text-gray-600">
                                <span>Shipping</span>
                                <span className="text-green-600 font-bold">Free</span>
                            </div>
                            <div className="border-t pt-3 flex justify-between text-lg font-bold text-gray-900">
                                <span>Total</span>
                                <span>${totals.price.toFixed(2)}</span>
                            </div>
                        </div>

                        <Link to="/checkout" className="w-full bg-eco-green text-white py-3 rounded-xl font-bold flex items-center justify-center gap-2 hover:bg-eco-dark transition shadow-md">
                            Proceed to Checkout <ArrowRight size={18} />
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Cart;