import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShoppingCart, Leaf, ArrowLeft, ShieldCheck, Heart } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import api from '../api/axios';
import { getImageSrc, handleImageError } from '../utils/imageUtils';

// Chart imports
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';

// NEW: Import Reviews Component
import ProductReviews from '../components/ProductReviews';

ChartJS.register(ArcElement, Tooltip, Legend);

const ProductDetails = () => {
    const { id } = useParams();
    const { user } = useAuth();
    const { fetchCartCount } = useCart();
    const navigate = useNavigate();
    
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    
    // Wishlist State
    const [inWishlist, setInWishlist] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // 1. Fetch Product
                const res = await api.get(`/products/${id}`);
                setProduct(res.data);

                // 2. Check Wishlist Status (if logged in)
                if (user) {
                    const wishRes = await api.get(`/wishlist/${user.id}/check/${id}`);
                    setInWishlist(wishRes.data?.inWishlist || false);
                }
            } catch (error) {
                console.error("Error loading details", error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [id, user]);

    const addToCart = async () => {
        if (!user) return navigate('/login');
        try {
            await api.post(`/cart/${user.id}/items`, { productId: product.id, quantity: 1 });
            await fetchCartCount();
            alert("Added to Cart!");
        } catch (error) {
            alert("Failed to add to cart");
        }
    };

    const toggleWishlist = async () => {
        if (!user) return navigate('/login');
        try {
            if (inWishlist) {
                await api.delete(`/wishlist/${user.id}/${product.id}`);
                setInWishlist(false);
            } else {
                await api.post(`/wishlist/${user.id}`, { productId: product.id });
                setInWishlist(true);
            }
        } catch (error) {
            console.error("Wishlist action failed");
        }
    };

    if (loading) return <div className="text-center py-20">Loading...</div>;
    if (!product) return <div className="text-center py-20">Product not found</div>;

    // Chart Data Config
    const carbonData = {
        labels: ['Manufacturing', 'Transport', 'Packaging', 'Usage', 'Disposal'],
        datasets: [{
            data: [
                product.carbonBreakdown?.manufacturing || 0,
                product.carbonBreakdown?.transportation || 0,
                product.carbonBreakdown?.packaging || 0,
                product.carbonBreakdown?.usage || 0,
                product.carbonBreakdown?.disposal || 0,
            ],
            backgroundColor: ['#10B981', '#3B82F6', '#F59E0B', '#6366F1', '#EF4444'],
            borderWidth: 1,
        }],
    };

    const imageSource = getImageSrc(product.imageBase64 || product.image || product.imageUrl);

    return (
        <div className="max-w-6xl mx-auto">
            <button onClick={() => navigate(-1)} className="flex items-center text-gray-500 hover:text-eco-green mb-6">
                <ArrowLeft size={20} className="mr-1" /> Back
            </button>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-16">
                {/* Image */}
                <div className="bg-white p-4 rounded-2xl shadow-sm border border-gray-100 flex items-center justify-center">
                    <img 
                        src={imageSource} 
                        alt={product.name} 
                        onError={handleImageError}
                        className="w-full h-96 object-contain rounded-lg" 
                    />
                </div>

                {/* Details */}
                <div>
                    <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center gap-2">
                            <span className="bg-gray-100 text-gray-600 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">
                                {product.category}
                            </span>
                            {product.verified && (
                                <span className="flex items-center gap-1 text-blue-600 text-xs font-bold bg-blue-50 px-2 py-1 rounded-full">
                                    <ShieldCheck size={12} /> Verified
                                </span>
                            )}
                        </div>
                        
                        {/* Wishlist Button */}
                        <button 
                            onClick={toggleWishlist}
                            className={`p-2 rounded-full border transition ${
                                inWishlist 
                                    ? 'bg-red-50 border-red-200 text-red-500' 
                                    : 'border-gray-200 text-gray-400 hover:text-red-500 hover:bg-red-50'
                            }`}
                            title={inWishlist ? "Remove from Wishlist" : "Add to Wishlist"}
                        >
                            <Heart size={20} className={inWishlist ? "fill-current" : ""} />
                        </button>
                    </div>

                    <h1 className="text-3xl font-bold text-gray-900 mb-4">{product.name}</h1>
                    <p className="text-gray-600 mb-6 leading-relaxed">{product.description}</p>

                    <div className="flex items-center gap-6 mb-8 p-4 bg-gray-50 rounded-xl">
                        <div>
                            <span className="text-xs text-gray-400 block">Price</span>
                            <span className="text-2xl font-bold text-gray-900">
                                ${typeof product.price === 'number' ? product.price.toFixed(2) : product.price}
                            </span>
                        </div>
                        <div className="border-r h-8 border-gray-200"></div>
                        <div>
                            <span className="text-xs text-gray-400 block">Eco Rating</span>
                            <span className="text-2xl font-bold text-eco-green">{product.ecoRating}</span>
                        </div>
                        <div className="border-r h-8 border-gray-200"></div>
                        <div>
                            <span className="text-xs text-gray-400 block">Total Carbon</span>
                            <span className="text-2xl font-bold text-gray-700">{product.carbonFootprint} kg</span>
                        </div>
                    </div>

                    <button 
                        onClick={addToCart}
                        className="w-full bg-eco-green text-white py-4 rounded-xl font-bold flex items-center justify-center gap-2 hover:bg-eco-dark transition shadow-lg shadow-eco-green/20"
                    >
                        <ShoppingCart size={20} /> Add to Cart
                    </button>
                </div>
            </div>

            {/* Carbon Footprint Breakdown Chart */}
            <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 mb-16">
                <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
                    <Leaf className="text-eco-green" /> Carbon Lifecycle Breakdown (kg CO₂e)
                </h3>
                <div className="h-64 flex justify-center">
                    <Pie data={carbonData} options={{ maintainAspectRatio: false }} />
                </div>
            </div>

            {/* Product Reviews Section */}
            <ProductReviews productId={product.id} />
        </div>
    );
};

export default ProductDetails;