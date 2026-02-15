import { useEffect, useState } from 'react';
import { Search, Filter, Leaf } from 'lucide-react';
import api from '../api/axios';
import ProductCard from '../components/ProductCard';

const Shop = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showFilters, setShowFilters] = useState(false);

    // Filters State
    const [search, setSearch] = useState('');
    const [category, setCategory] = useState('All');
    const [sort, setSort] = useState('newest');
    const [maxPrice, setMaxPrice] = useState(1000);
    const [maxCarbon, setMaxCarbon] = useState(100);

    useEffect(() => {
        fetchProducts();
    }, [category, sort, maxPrice, maxCarbon, search]);

    const fetchProducts = async () => {
        setLoading(true);
        try {
            // Build Query String
            const params = new URLSearchParams();
            
            // FIX 1: Changed 'name' to 'search' to match ProductController
            if (search) params.append('search', search); 
            
            if (category !== 'All') params.append('category', category);
            if (maxPrice < 1000) params.append('maxPrice', maxPrice);
            if (maxCarbon < 100) params.append('maxCarbon', maxCarbon);
            params.append('sortBy', sort);
            
            // Call the Search API
            const response = await api.get(`/products?${params.toString()}`);
            
            // FIX 2: Correctly extract the list from the "products" key
            // The controller returns { products: [...], totalPages: ... }
            const data = response.data.products || []; 
            
            setProducts(data);
        } catch (error) {
            console.error("Shop Load Error:", error);
            setProducts([]); // Safety fallback
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col md:flex-row gap-8 max-w-7xl mx-auto">
            
            {/* --- MOBILE FILTER TOGGLE --- */}
            <button 
                className="md:hidden flex items-center gap-2 bg-gray-100 p-3 rounded-lg font-bold"
                onClick={() => setShowFilters(!showFilters)}
            >
                <Filter size={20} /> Filters
            </button>

            {/* --- SIDEBAR FILTERS --- */}
            <div className={`w-full md:w-64 flex-shrink-0 ${showFilters ? 'block' : 'hidden md:block'}`}>
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 sticky top-24">
                    <h3 className="font-bold text-lg mb-6 flex items-center gap-2">
                        <Filter size={20} /> Filters
                    </h3>

                    {/* Category Filter */}
                    <div className="mb-6">
                        <label className="block text-sm font-bold text-gray-700 mb-2">Category</label>
                        <select 
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                            className="w-full p-2 border rounded-lg bg-gray-50 text-sm"
                        >
                            <option value="All">All Categories</option>
                            <option>Electronics</option>
                            <option>Clothing & Apparel</option>
                            <option>Home & Kitchen</option>
                            <option>Food & Beverages</option>
                            <option>Beauty & Personal Care</option>
                        </select>
                    </div>

                    {/* Price Range */}
                    <div className="mb-6">
                        <div className="flex justify-between text-sm mb-2">
                            <label className="font-bold text-gray-700">Max Price</label>
                            <span className="text-eco-green font-bold">${maxPrice}</span>
                        </div>
                        <input 
                            type="range" min="0" max="1000" step="10" 
                            value={maxPrice}
                            onChange={(e) => setMaxPrice(e.target.value)}
                            className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-eco-green"
                        />
                    </div>

                    {/* Carbon Footprint Filter */}
                    <div className="mb-6">
                        <div className="flex justify-between text-sm mb-2">
                            <label className="font-bold text-gray-700 flex items-center gap-1">
                                <Leaf size={12} /> Max Carbon
                            </label>
                            <span className="text-eco-green font-bold">{maxCarbon}kg</span>
                        </div>
                        <input 
                            type="range" min="0" max="100" step="1" 
                            value={maxCarbon}
                            onChange={(e) => setMaxCarbon(e.target.value)}
                            className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-eco-green"
                        />
                        <p className="text-xs text-gray-400 mt-2">Filter out high-emission items.</p>
                    </div>

                    <button 
                        onClick={() => {setCategory('All'); setMaxPrice(1000); setMaxCarbon(100); setSearch('')}}
                        className="w-full py-2 text-sm text-gray-500 hover:text-red-500 border border-gray-200 rounded-lg hover:border-red-200 transition"
                    >
                        Reset Filters
                    </button>
                </div>
            </div>

            {/* --- MAIN PRODUCT GRID --- */}
            <div className="flex-grow">
                {/* Search & Sort Bar */}
                <div className="flex flex-col md:flex-row justify-between items-center mb-6 gap-4">
                    <div className="relative w-full md:w-96">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                        <input 
                            type="text" 
                            placeholder="Search sustainable products..." 
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-eco-green/20"
                        />
                    </div>

                    <div className="flex items-center gap-2 w-full md:w-auto">
                        <span className="text-sm text-gray-500 whitespace-nowrap">Sort by:</span>
                        <select 
                            value={sort}
                            onChange={(e) => setSort(e.target.value)}
                            className="p-2 border rounded-lg bg-white text-sm focus:outline-none"
                        >
                            <option value="newest">Newest Arrivals</option>
                            <option value="price_asc">Price: Low to High</option>
                            <option value="price_desc">Price: High to Low</option>
                            <option value="carbon_asc">🌱 Eco: Low Carbon First</option>
                        </select>
                    </div>
                </div>

                {/* Product List */}
                {loading ? (
                    <div className="text-center py-20">Loading Catalog...</div>
                ) : products.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {products.map(product => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-20 bg-gray-50 rounded-xl border border-dashed border-gray-200">
                        <Leaf className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                        <h3 className="text-lg font-bold text-gray-600">No products found</h3>
                        <p className="text-gray-400">Try adjusting your filters or search terms.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Shop;