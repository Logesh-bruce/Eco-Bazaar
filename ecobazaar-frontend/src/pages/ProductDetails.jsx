import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShoppingCart, Leaf, ArrowLeft, ShieldCheck, Heart } from 'lucide-react'; // Added Heart
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import api from '../api/axios';

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
    
    // NEW: Wishlist State
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
                    setInWishlist(wishRes.data.inWishlist);
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

    return (
        <div className="max-w-6xl mx-auto">
            <button onClick={() => navigate(-1)} className="flex items-center text-gray-500 hover:text-eco-green mb-6">
                <ArrowLeft size={20} className="mr-1" /> Back
            </button>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-16">
                {/* Image */}
                <div className="bg-white p-4 rounded-2xl shadow-sm border border-gray-100">
                    <img src={product.imageUrl} alt={product.name} className="w-full h-96 object-contain rounded-lg" />
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
                        
                        {/* NEW: Wishlist Button */}
                        <button 
                            onClick={toggleWishlist}
                            className={`p-3 rounded-full transition ${inWishlist ? 'bg-red-50 text-red-500' : 'bg-gray-100 text-gray-400 hover:bg-gray-200'}`}
                            title={inWishlist ? "Remove from Wishlist" : "Add to Wishlist"}
                        >
                            <Heart fill={inWishlist ? "currentColor" : "none"} size={24} />
                        </button>
                    </div>

                    <h1 className="text-4xl font-bold text-gray-900 mb-4">{product.name}</h1>
                    
                    <div className="flex items-center gap-4 mb-6">
                        <span className="text-3xl font-bold text-eco-green">${product.price.toFixed(2)}</span>
                        <div className={`px-3 py-1 rounded text-sm font-bold text-white
                            ${product.ecoRating === 'A+' ? 'bg-green-600' : 
                              product.ecoRating === 'B' ? 'bg-yellow-500' : 'bg-red-500'}`}>
                            Eco Rating: {product.ecoRating}
                        </div>
                    </div>

                    <p className="text-gray-600 leading-relaxed mb-8">{product.description}</p>

                    <button 
                        onClick={addToCart}
                        className="w-full md:w-auto bg-gray-900 text-white px-8 py-4 rounded-xl font-bold text-lg flex items-center justify-center gap-2 hover:bg-eco-green transition shadow-lg shadow-gray-200"
                    >
                        <ShoppingCart /> Add to Cart
                    </button>

                    {/* Carbon Footprint */}
                    <div className="mt-12 bg-green-50 rounded-2xl p-6 border border-green-100">
                        <h3 className="text-xl font-bold text-gray-800 mb-6 flex items-center gap-2">
                            <Leaf className="text-eco-green" /> Carbon Footprint Analysis
                        </h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
                            <div className="w-48 mx-auto"><Pie data={carbonData} /></div>
                            <div>
                                <p className="text-gray-600 mb-4">
                                    Total Emission: <span className="font-bold text-gray-900 text-xl">{product.carbonFootprint} kg CO₂e</span>
                                </p>
                                <p className="text-xs text-green-700 italic">
                                    *Detailed breakdown by lifecycle stage.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* NEW: Review Section */}
            <ProductReviews productId={product.id} />
        </div>
    );
};

export default ProductDetails;