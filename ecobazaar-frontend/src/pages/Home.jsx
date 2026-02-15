import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Leaf, ShieldCheck, Truck } from 'lucide-react';
import api from '../api/axios';
import ProductCard from '../components/ProductCard';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                // Fetch featured products from your ProductController
                const response = await api.get('/products/featured');
                setProducts(response.data);
            } catch (error) {
                console.error("Failed to fetch products:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    return (
        <div>
            {/* Hero Section */}
            <section className="bg-eco-dark text-white rounded-3xl p-8 md:p-16 mb-12 relative overflow-hidden">
                <div className="relative z-10 max-w-2xl">
                    <span className="bg-green-500/20 text-green-300 px-3 py-1 rounded-full text-sm font-semibold mb-4 inline-block">
                        Sustainable Marketplace
                    </span>
                    <h1 className="text-4xl md:text-6xl font-bold mb-6 leading-tight">
                        Shop Responsibly, <br/> 
                        <span className="text-eco-green">Live Sustainably.</span>
                    </h1>
                    <p className="text-gray-300 text-lg mb-8">
                        Every purchase comes with a transparent Carbon Footprint score. 
                        Join us in reducing CO₂ emissions one order at a time.
                    </p>
                    <Link to="/products" className="inline-flex items-center gap-2 bg-eco-green text-white px-8 py-3 rounded-full font-bold hover:bg-green-600 transition">
                        Shop Now <ArrowRight size={20} />
                    </Link>
                </div>
                {/* Decorative Circle */}
                <div className="absolute top-0 right-0 w-96 h-96 bg-green-500/10 rounded-full blur-3xl -mr-16 -mt-16"></div>
            </section>

            {/* Features Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
                <div className="flex items-center gap-4 p-6 bg-white rounded-xl shadow-sm border border-gray-100">
                    <div className="bg-green-100 p-3 rounded-full text-eco-green"><Leaf /></div>
                    <div>
                        <h3 className="font-bold text-lg">Eco-Friendly</h3>
                        <p className="text-gray-500 text-sm">Curated sustainable items</p>
                    </div>
                </div>
                <div className="flex items-center gap-4 p-6 bg-white rounded-xl shadow-sm border border-gray-100">
                    <div className="bg-blue-100 p-3 rounded-full text-blue-600"><ShieldCheck /></div>
                    <div>
                        <h3 className="font-bold text-lg">Verified Sellers</h3>
                        <p className="text-gray-500 text-sm">Quality you can trust</p>
                    </div>
                </div>
                <div className="flex items-center gap-4 p-6 bg-white rounded-xl shadow-sm border border-gray-100">
                    <div className="bg-yellow-100 p-3 rounded-full text-yellow-600"><Truck /></div>
                    <div>
                        <h3 className="font-bold text-lg">Carbon Neutral</h3>
                        <p className="text-gray-500 text-sm">Offset shipping options</p>
                    </div>
                </div>
            </div>

            {/* Featured Products Section */}
            <div className="mb-8 flex justify-between items-end">
                <div>
                    <h2 className="text-3xl font-bold text-gray-800">Featured Products</h2>
                    <p className="text-gray-500 mt-2">Handpicked items for a greener lifestyle</p>
                </div>
                <Link to="/products" className="text-eco-green font-semibold hover:underline">
                    View All Products
                </Link>
            </div>

            {loading ? (
                <div className="text-center py-20 text-gray-500">Loading sustainable goodies...</div>
            ) : products.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    {products.map(product => (
                        <ProductCard key={product.id} product={product} />
                    ))}
                </div>
            ) : (
                <div className="text-center py-20 bg-gray-50 rounded-xl border border-dashed border-gray-300">
                    <h3 className="text-xl font-medium text-gray-600">No products found?</h3>
                    <p className="text-gray-500 mt-2">Log in as a <span className="font-bold">Seller</span> to add the first product!</p>
                </div>
            )}
        </div>
    );
};

export default Home;