import { useEffect, useState } from 'react';
import { Heart, ShoppingCart, Trash2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import api from '../api/axios';

const Wishlist = () => {
    const { user } = useAuth();
    const { fetchCartCount } = useCart();
    const [wishlist, setWishlist] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) return;
        const fetchWishlist = async () => {
            try {
                const response = await api.get(`/wishlist/${user.id}`);
                setWishlist(response.data);
            } catch (error) {
                console.error("Failed to load wishlist", error);
            } finally {
                setLoading(false);
            }
        };
        fetchWishlist();
    }, [user]);

    const removeFromWishlist = async (productId) => {
        try {
            await api.delete(`/wishlist/${user.id}/${productId}`);
            setWishlist(wishlist.filter(item => item.productId !== productId));
        } catch (error) {
            alert("Failed to remove item");
        }
    };

    const moveToCart = async (product) => {
        try {
            await api.post(`/cart/${user.id}/items`, {
                productId: product.productId,
                quantity: 1
            });
            await fetchCartCount();
            alert("Moved to Cart!");
            removeFromWishlist(product.productId);
        } catch (error) {
            alert("Failed to add to cart");
        }
    };

    if (loading) return <div className="text-center py-20">Loading Wishlist...</div>;

    return (
        <div className="max-w-6xl mx-auto">
            <h1 className="text-3xl font-bold mb-8 flex items-center gap-2">
                <Heart className="text-red-500 fill-current" /> My Wishlist
            </h1>

            {wishlist.length === 0 ? (
                <div className="text-center py-20 bg-gray-50 rounded-xl border border-dashed">
                    <Heart className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                    <p className="text-gray-500">Your wishlist is empty.</p>
                    <Link to="/products" className="text-eco-green font-bold hover:underline mt-2 inline-block">
                        Browse Products
                    </Link>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {wishlist.map(item => (
                        <div key={item.id} className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col">
                            <div className="relative h-48 mb-4 bg-gray-100 rounded-lg overflow-hidden">
                                <img src={item.image} alt={item.productName} className="w-full h-full object-cover" />
                                <button 
                                    onClick={() => removeFromWishlist(item.productId)}
                                    className="absolute top-2 right-2 p-2 bg-white rounded-full shadow hover:bg-red-50 text-gray-400 hover:text-red-500 transition"
                                >
                                    <Trash2 size={18} />
                                </button>
                            </div>
                            
                            <h3 className="font-bold text-lg mb-1 line-clamp-1">{item.productName}</h3>
                            <p className="text-eco-green font-bold text-xl mb-4">${item.price.toFixed(2)}</p>
                            
                            <button 
                                onClick={() => moveToCart(item)}
                                className="mt-auto w-full bg-gray-900 text-white py-2 rounded-lg font-bold hover:bg-eco-green transition flex items-center justify-center gap-2"
                            >
                                <ShoppingCart size={18} /> Move to Cart
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Wishlist;