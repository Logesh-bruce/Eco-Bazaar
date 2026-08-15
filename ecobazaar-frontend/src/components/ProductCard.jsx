import { ShoppingCart, Leaf } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import api from '../api/axios';
import { getImageSrc, handleImageError } from '../utils/imageUtils';

const ProductCard = ({ product }) => {
    const { user } = useAuth();
    const { fetchCartCount } = useCart();

    const addToCart = async () => {
        if (!user) {
            alert("Please login to buy items!");
            return;
        }
        
        try {
            await api.post(`/cart/${user.id}/items`, {
                productId: product.id,
                quantity: 1
            });
            
            await fetchCartCount();
            alert("Added to Cart!");
        } catch (error) {
            console.error("Add to cart failed", error);
            alert("Failed to add to cart");
        }
    };

    const imageSource = getImageSrc(product.imageBase64 || product.image || product.imageUrl);

    return (
        <div className="bg-white rounded-xl shadow-sm hover:shadow-md transition border border-gray-100 overflow-hidden flex flex-col h-full">
            <div className="h-48 overflow-hidden bg-gray-100 relative group">
                <img 
                    src={imageSource} 
                    alt={product.name} 
                    onError={handleImageError}
                    className="w-full h-full object-contain transition duration-300"
                />

                <div className={`absolute top-2 right-2 px-2 py-1 rounded text-xs font-bold text-white
                    ${product.ecoRating === 'A+' ? 'bg-green-600' : 
                      product.ecoRating === 'B' ? 'bg-yellow-500' : 'bg-red-500'}`}>
                    Eco: {product.ecoRating}
                </div>
            </div>

            <div className="p-4 flex flex-col flex-grow">
                <div className="flex justify-between items-start mb-2">
                    <span className="text-xs font-semibold text-gray-500 uppercase tracking-wider">
                        {product.category || 'General'}
                    </span>
                    <div className="flex items-center text-eco-green text-xs gap-1">
                        <Leaf size={12} />
                        <span>{product.carbonFootprint}kg CO₂</span>
                    </div>
                </div>

                <Link to={`/product/${product.id}`} className="text-lg font-bold text-gray-800 hover:text-eco-green transition mb-1">
                    {product.name}
                </Link>
                
                <p className="text-gray-500 text-sm line-clamp-2 mb-4 flex-grow">
                    {product.description}
                </p>

                <div className="flex items-center justify-between mt-auto pt-4 border-t border-gray-100">
                    <span className="text-xl font-bold text-gray-900">
                        ${typeof product.price === 'number' ? product.price.toFixed(2) : product.price}
                    </span>
                    
                    {user?.role === 'SELLER' ? (
                        <span className="text-xs text-gray-400">Seller View</span>
                    ) : (
                        <button 
                            onClick={addToCart}
                            className="bg-gray-900 text-white p-2 rounded-full hover:bg-eco-green transition"
                            title="Add to Cart"
                        >
                            <ShoppingCart size={20} />
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProductCard;